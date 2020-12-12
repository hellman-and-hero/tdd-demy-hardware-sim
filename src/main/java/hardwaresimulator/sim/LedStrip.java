package hardwaresimulator.sim;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LedStrip implements Iterable<LevelMeter> {

	private final List<LevelMeter> levelMeters;

	public LedStrip(List<? extends LevelMeter> levelMeters) {
		this.levelMeters = new ArrayList<>(levelMeters);
	}

	@Override
	public Iterator<LevelMeter> iterator() {
		return levelMeters.iterator();
	}

	public void switchLed(int led, Color color) {
		for (LevelMeter levelMeter : levelMeters) {
			int ledsOnRing = levelMeter.getLedCount();
			if (led < ledsOnRing) {
				levelMeter.setColor(led, color);
				return;
			}
			led -= ledsOnRing;
		}
	}

}
