package hardwaresimulator;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.SwingUtilities.invokeLater;
import hardwaresimulator.mqtt.Message;
import hardwaresimulator.mqtt.MqttConsumer;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {

	private static final String MQTT_HOST = "127.0.0.1";
	private static final int MQTT_PORT = 1883;
	private static final int RINGS = 4;
	private static final int LED_COUNT = 16;

	private LevelMeters levelMeters = new LevelMeters(RINGS, () -> newLevelMeter());
	private final Pattern topicPattern = compile("some/led/(\\d+)/rgb");
	private MqttConsumer mqtt;

	private static LevelMeter newLevelMeter() {
		return new LevelMeter(LED_COUNT).withLedSize(12);
	}

	public static void main(String[] args) throws IOException {
		new Main();
	}

	public Main() throws IOException {
		mqtt = new MqttConsumer(MQTT_HOST, MQTT_PORT);
		mqtt.addConsumer(this::consume);
		invokeLater(() -> createAndShowGUI());
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
