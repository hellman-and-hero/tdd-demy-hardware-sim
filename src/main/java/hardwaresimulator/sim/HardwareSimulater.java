package hardwaresimulator.sim;

import static java.util.stream.IntStream.range;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import hardwaresimulator.sim.MqttLedStripService.Config;

public class HardwareSimulater extends JFrame {

	private static final long serialVersionUID = -3260281746665785048L;

	public HardwareSimulater(Config config) throws IOException {
		super("Hardware Simulator");

		List<JLevelMeter> levelMeters = range(0, config.rings()).mapToObj(__ -> newLevelMeter(config)).toList();
		MqttLedStripService service = new MqttLedStripService(config.mqttHost(), config.mqttPort(), levelMeters) {
			@Override
			protected void switchLed(Led led, Color color) {
				invokeLater(() -> super.switchLed(led, color));
			}
		};

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent __) {
				try {
					service.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});

		setLayout(new FlowLayout());
		JPanel panel = new JPanel();
		levelMeters.forEach(panel::add);
		getContentPane().add(panel);

		pack();
		setResizable(false);
	}

	private JLevelMeter newLevelMeter(Config config) {
		return new JLevelMeter(config.ledCount()).withSize(config.ringSize()).withLedSize(config.ledSize());
	}

}
