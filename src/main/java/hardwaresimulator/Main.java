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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import hardwaresimulator.mqtt.Message;
import hardwaresimulator.mqtt.MqttConsumer;

public class Main {

	private LevelMeters levelMeters;
	private final Pattern topicPattern = compile("some/led/(\\d+)/rgb");
	private MqttConsumer mqtt;

	public static void main(String... args) throws IOException {
		Main main = new Main();
		main.tryParseArgs(args).ifPresent(main::startup);
	}

	private void startup(CommandLineArguments args) {
		try {
			mqtt = new MqttConsumer(args.mqttHost, args.mqttPort);
			mqtt.addConsumer(this::consume);

			levelMeters = new LevelMeters(args.rings, () -> newLevelMeter(args));
			invokeLater(() -> createAndShowGUI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private LevelMeter newLevelMeter(CommandLineArguments cmdLineArgs) {
		return new LevelMeter(cmdLineArgs.ledCount).withSize(cmdLineArgs.ringSize).withLedSize(cmdLineArgs.ledSize);
	}

	private Optional<CommandLineArguments> tryParseArgs(String... args) {
		CommandLineArguments cmdLineArgs = new CommandLineArguments();
		CmdLineParser parser = new CmdLineParser(cmdLineArgs, defaults().withUsageWidth(80));
		try {
			parser.parseArgument(args);
			if (!cmdLineArgs.help) {
				return Optional.of(cmdLineArgs);
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
		}
		printHelp(parser);
		return Optional.empty();
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
