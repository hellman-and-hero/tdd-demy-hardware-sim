package hardwaresimulator;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.SwingUtilities.invokeLater;
import static org.kohsuke.args4j.OptionHandlerFilter.ALL;
import static org.kohsuke.args4j.ParserProperties.defaults;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import hardwaresimulator.mqtt.Message;
import hardwaresimulator.mqtt.MqttConsumer;

public class Main {

	@Option(name = "-h", help = true)
	public boolean help;

	@Option(name = "-mqttHost", usage = "hostname of the mqtt broker")
	public String mqttHost = "127.0.0.1";
	@Option(name = "-mqttPort", usage = "port of the mqtt broker")
	public int mqttPort = 1883;

	@Option(name = "-rings", usage = "how many leds rings should be simulated")
	public int rings = 4;
	@Option(name = "-ledCount", usage = "how many leds should each ring have")
	public int ledCount = 16;
	@Option(name = "-ledSize", usage = "size in pixels of each led")
	public int ledSize = 12;

	private LevelMeters levelMeters;
	private final Pattern topicPattern = compile("some/led/(\\d+)/rgb");
	private MqttConsumer mqtt;

	private LevelMeter newLevelMeter() {
		return new LevelMeter(ledCount).withLedSize(ledSize);
	}

	public static void main(String... args) throws IOException {
		Main main = new Main();
		if (main.parseArgs(args)) {
			main.startup();
		}
	}

	private void startup() throws IOException {
		mqtt = new MqttConsumer(mqttHost, mqttPort);
		mqtt.addConsumer(this::consume);

		levelMeters = new LevelMeters(rings, () -> newLevelMeter());
		invokeLater(() -> createAndShowGUI());
	}

	private boolean parseArgs(String... args) {
		CmdLineParser parser = new CmdLineParser(this, defaults().withUsageWidth(80));
		try {
			parser.parseArgument(args);
			if (help) {
				printHelp(parser);
				return false;
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			printHelp(parser);
			return false;
		}
		return true;
	}

	private void printHelp(CmdLineParser parser) {
		String mainClassName = getClass().getName();
		System.err.println("java " + mainClassName + " [options...] arguments...");
		parser.printUsage(System.err);
		System.err.println();
		System.err.println("  Example: java " + mainClassName + parser.printExample(ALL));
	}

	private void consume(Message message) {
		System.out.println(format("Received %s %s", message.getTopic(), message.getPayload()));
		Matcher matcher = topicPattern.matcher(message.getTopic());
		if (matcher.matches()) {
			levelMeters.switchLed(Integer.parseInt(matcher.group(1)), Color.decode(message.getPayload()));
		}
	}

	private void createAndShowGUI() {
		JFrame frame = new JFrame("Hardware Simulator");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				try {
					mqtt.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);

		frame.setLayout(new FlowLayout());
		levelMeters.forEach(m -> panel.add(m));
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

}
