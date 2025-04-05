package hardwaresimulator.sim;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class LedStrip {

	private final List<LevelMeter> levelMeters;

	public LedStrip(List<? extends LevelMeter> levelMeters) {
		this.levelMeters = new ArrayList<>(levelMeters);
	}

	public boolean switchLed(int led, Color color) {
		if (led >= 0) {
			for (LevelMeter levelMeter : levelMeters) {
				int ledsOnRing = levelMeter.getLedCount();
				if (led < ledsOnRing) {
					levelMeter.setColor(led, color);
					return true;
				}
				led -= ledsOnRing;
			}
		}
		return false;
	}

}
