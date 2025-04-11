package hardwaresimulator.sim;

import static hardwaresimulator.sim.Led.led;

import java.awt.Color;
import java.util.List;

public class LedStrip {

	private final List<LevelMeter> levelMeters;

	public LedStrip(List<? extends LevelMeter> levelMeters) {
		this.levelMeters = List.copyOf(levelMeters);
	}

	public boolean switchLed(Led led, Color color) {
		int index = led.index();
		assert index >= 0 : "expect led index not to be negative but was " + index;
		for (LevelMeter levelMeter : levelMeters) {
			int ledsOnRing = levelMeter.getLedCount();
			if (index < ledsOnRing) {
				levelMeter.setColor(led(index), color);
				return true;
			}
			index -= ledsOnRing;
		}
		return false;
	}

}
