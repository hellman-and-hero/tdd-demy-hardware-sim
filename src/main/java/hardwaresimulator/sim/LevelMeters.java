package hardwaresimulator.sim;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class LevelMeters implements Iterable<LevelMeter> {

	private final List<LevelMeter> levelMeters;

	public LevelMeters(int rings, Supplier<LevelMeter> supplier) {
		this(range(0, rings).mapToObj(i -> supplier.get()).collect(toList()));
	}

	public LevelMeters(List<LevelMeter> levelMeters) {
		this.levelMeters = new ArrayList<LevelMeter>(levelMeters);
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
