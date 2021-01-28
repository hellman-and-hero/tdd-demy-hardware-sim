package hardwaresimulator.sim;

import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;

import hardwaresimulator.mqtt.Message;
import hardwaresimulator.mqtt.MqttConsumer;

public class HardwareSimulater {

	public interface Config {
		String mqttHost();

		int mqttPort();

		int rings();

		int ledCount();

		int ringSize();

		int ledSize();
	}

	private final MqttConsumer mqtt;
	private final LedStrip ledStrip;
	private final String topicPrefix = "some/led/";
	private final Pattern topicPattern = compile(topicPrefix + "(\\d+)/rgb");
	private List<JLevelMeter> levelMeters;

	public HardwareSimulater(Config config) {
		try {
			mqtt = new MqttConsumer(config.mqttHost(), config.mqttPort(), topicPrefix + "#");
			mqtt.addConsumer(this::consume);
			levelMeters = range(0, config.rings()).mapToObj(i -> newLevelMeter(config)).collect(toList());
			ledStrip = new LedStrip(levelMeters);
			invokeLater(() -> createAndShowGUI());
		} catch (Exception e) {
			throw new RuntimeException(e);
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
		levelMeters.forEach(panel::add);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private void consume(Message message) {
		// we could debug, but if a team fails sending the right messages we do not want
		// to be too verbose ;-)
		// System.out.format("Received %s %s\n", message.getTopic(),
		// message.getPayload());
		Matcher matcher = topicPattern.matcher(message.getTopic());
		if (matcher.matches()) {
			ledStrip.switchLed(Integer.parseInt(matcher.group(1)), Color.decode(message.getPayload()));
		}
	}

	private JLevelMeter newLevelMeter(Config config) {
		return new JLevelMeter(config.ledCount()).withSize(config.ringSize()).withLedSize(config.ledSize());
	}

}
