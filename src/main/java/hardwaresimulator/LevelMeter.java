package hardwaresimulator;

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

public class LevelMeter extends JPanel {

	private static final long serialVersionUID = 7567178446520016358L;

	private final Color[] ledColors;
	private int width = 200, height = 200;
	private final int radius = width / 2 - 40;
	private int ledSize = 26;

	public LevelMeter(int leds) {
		ledColors = new Color[leds];
		fill(ledColors, BLACK);
		setLayout(null);
		sizes();
	}

	private void sizes() {
		Dimension dim = new Dimension(width, height);
		setMinimumSize(dim);
		setMaximumSize(dim);
		setPreferredSize(dim);
		repaint();
	}

	public LevelMeter withLedSize(int ledSize) {
		this.ledSize = ledSize;
		return this;
	}

	public LevelMeter withSize(int size) {
		this.width = size;
		this.height = size;
		return this;
	}

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
		g2.fillOval(ledCoordinates.x - (ledSize / 2), ledCoordinates.y
				- (ledSize / 2), ledSize, ledSize);
	}

	private Point ledCoordinates(int step) {
		double t = 2 * PI * (step + 1 + (ledColors.length / 4))
				/ ledColors.length;
		return new Point(x(t), y(t));
	}

	private int x(double t) {
		return (int) (width / 2 + radius * cos(t));
	}

	private int y(double t) {
		return (int) (height / 2 + radius * sin(t));
	}

	public int getLedCount() {
		return ledColors.length;
	}

}
