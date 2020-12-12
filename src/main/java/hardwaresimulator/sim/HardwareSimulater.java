package hardwaresimulator.sim;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
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
	private final LevelMeters levelMeters;
	private final Pattern topicPattern = compile("some/led/(\\d+)/rgb");

	public HardwareSimulater(Config config) {
		try {
			mqtt = new MqttConsumer(config.mqttHost(), config.mqttPort());
			mqtt.addConsumer(this::consume);

			levelMeters = new LevelMeters(config.rings(), () -> newLevelMeter(config));
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
		levelMeters.forEach(m -> panel.add(m));
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private void consume(Message message) {
		System.out.println(format("Received %s %s", message.getTopic(), message.getPayload()));
		Matcher matcher = topicPattern.matcher(message.getTopic());
		if (matcher.matches()) {
			levelMeters.switchLed(Integer.parseInt(matcher.group(1)), Color.decode(message.getPayload()));
		}
	}

	private LevelMeter newLevelMeter(Config config) {
		return new LevelMeter(config.ledCount()).withSize(config.ringSize()).withLedSize(config.ledSize());
	}

}
