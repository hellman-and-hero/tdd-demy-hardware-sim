package hardwaresimulator;

import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.SwingUtilities.invokeLater;
import hardwaresimulator.mqtt.MqttConsumer;

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

public class Main {

	private static final String MQTT_HOST = "192.168.1.1";
	private static final int MQTT_PORT = 1883;
	private static final int RINGS = 4;

	private static final List<LevelMeter> levelMeters = range(0, RINGS)
			.mapToObj(i -> newLevelMeter()).collect(toList());

	private static final Pattern topicPattern = compile("some/led/"
			+ "(\\d+)/rgb");
	private static MqttConsumer mqtt;

	public static void main(String[] args) throws IOException {
		mqtt = new MqttConsumer(MQTT_HOST, MQTT_PORT);
		mqtt.addConsumer(m -> {
			System.out.println("Got " + m.getTopic() + " " + m.getPayload());
			Matcher matcher = topicPattern.matcher(m.getTopic());
			if (matcher.matches()) {
				int led = Integer.parseInt(matcher.group(1));
				int ring = 0;
				while (led >= levelMeters.get(ring).getLedCount()) {
					led -= levelMeters.get(ring).getLedCount();
					ring++;
				}
				levelMeters.get(ring).setColor(led,
						Color.decode(m.getPayload()));
			}
		});
		invokeLater(() -> createAndShowGUI());
	}

	private static void createAndShowGUI() {
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
		levelMeters.forEach(lm -> panel.add(lm));
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private static LevelMeter newLevelMeter() {
		return new LevelMeter(16).withLedSize(12);
	}

}
