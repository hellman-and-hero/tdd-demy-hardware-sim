package hardwaresimulator.sim;

import static java.awt.Color.BLACK;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.util.Arrays.fill;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JPanel;

public class JLevelMeter extends JPanel implements LevelMeter {

	private static final long serialVersionUID = 7567178446520016358L;

	public static final int DEFAULT_RING_SIZE = 200;
	public static final int DEFAULT_LED_SIZE = 16;

	private final Color[] ledColors;
	private int width = DEFAULT_RING_SIZE, height = DEFAULT_RING_SIZE;
	private int radius;
	private int ledSize = DEFAULT_LED_SIZE;

	public JLevelMeter(int leds) {
		setLayout(null);
		ledColors = new Color[leds];
		fill(ledColors, BLACK);
		sizes();
	}

	private void sizes() {
		radius = width / 2 - 40;
		Dimension dim = new Dimension(width, height);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setPreferredSize(dim);
		repaint();
	}

	public JLevelMeter withLedSize(int ledSize) {
		this.ledSize = ledSize;
		return this;
	}

	public JLevelMeter withSize(int size) {
		this.width = size;
		this.height = size;
		sizes();
		return this;
	}

	@Override
	public void setColor(int led, Color color) {
		ledColors[led] = color;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		g2.clearRect(0, 0, getWidth(), getHeight());
		for (int led = 0; led < ledColors.length; led++) {
			drawLed(g2, led);
		}
	}

	private void drawLed(Graphics2D g2, int led) {
		Point ledCoordinates = ledCoordinates(led);
		g2.setColor(ledColors[led]);
		g2.fillOval(ledCoordinates.x - (ledSize / 2), ledCoordinates.y - (ledSize / 2), ledSize, ledSize);
	}

	private Point ledCoordinates(int step) {
		double t = 2 * PI * (step + 1 + (ledColors.length / 4)) / ledColors.length;
		return new Point(x(t), y(t));
	}

	private int x(double t) {
		return (int) (width / 2 + radius * cos(t));
	}

	private int y(double t) {
		return (int) (height / 2 + radius * sin(t));
	}

	@Override
	public int getLedCount() {
		return ledColors.length;
	}

}
