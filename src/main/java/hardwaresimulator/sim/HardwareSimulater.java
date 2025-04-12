package hardwaresimulator.sim;

import static java.util.stream.IntStream.range;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import hardwaresimulator.sim.MqttLedStripService.Config;

public class HardwareSimulater implements AutoCloseable {

	private final List<JLevelMeter> levelMeters;
	private final MqttLedStripService service;

	public HardwareSimulater(Config config) {
		try {
			levelMeters = range(0, config.rings()).mapToObj(__ -> newLevelMeter(config)).toList();
			service = new MqttLedStripService(config, new LedStrip(levelMeters)) {
				@Override
				protected void switchLed(Led led, Color color) {
					invokeLater(() -> super.switchLed(led, color));
				}
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public HardwareSimulater show() {
		invokeLater(this::createAndShowGUI);
		return this;
	}

	private void createAndShowGUI() {
		JFrame frame = new JFrame("Hardware Simulator");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent __) {
				try {
					close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});

		frame.setLayout(new FlowLayout());
		frame.getContentPane().add(panel());
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private JPanel panel() {
		JPanel panel = new JPanel();
		levelMeters.forEach(panel::add);
		return panel;
	}

	public boolean isConnected() {
		return service.isConnected();
	}

	@Override
	public void close() throws IOException {
		service.close();
	}

	public JLevelMeter levelMeters(int index) {
		return levelMeters.get(index);
	}

	protected JLevelMeter newLevelMeter(Config config) {
		return new JLevelMeter(config.ledCount()).withSize(config.ringSize()).withLedSize(config.ledSize());
	}

}
