package hardwaresimulator.sim;

import static java.awt.Color.decode;
import static java.util.regex.Pattern.compile;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hardwaresimulator.mqtt.Message;
import hardwaresimulator.mqtt.MqttClient;

public class MqttLedStripService implements AutoCloseable {

	public interface Config {
		String mqttHost();

		int mqttPort();

		int rings();

		int ledCount();

		int ringSize();

		int ledSize();
	}

	private final String topicPrefix = "some/led/";
	private final Pattern topicPattern = compile(topicPrefix + "(\\d+)/rgb");

	private final LedStrip ledStrip;
	private final MqttClient mqtt;

	public MqttLedStripService(String mqttHost, int mqttPort, List<? extends LevelMeter> levelMeters)
			throws IOException {
		this.ledStrip = new LedStrip(levelMeters);
		this.mqtt = new MqttClient(mqttHost, mqttPort, topicPrefix + "#");
		this.mqtt.addConsumer(this::consume);
	}

	private void consume(Message message) {
		// we could debug, but if a team fails sending the right messages we do not want
		// to be too verbose ;-)
		// System.out.format("Received %s %s\n", message.topic(), message.payload());
		Matcher matcher = topicPattern.matcher(message.topic());
		if (matcher.matches()) {
			invokeLater(() -> switchLed(Led.fromString(matcher.group(1)), decode(message.payload())));
		}
	}

	protected void switchLed(Led led, Color color) {
		ledStrip.switchLed(led, color);
	}

	@Override
	public void close() throws IOException {
		mqtt.close();
	}

}
