package hardwaresimulator.sim;

import java.awt.Color;

public interface LevelMeter {

	void setColor(Led led, Color color);

	int getLedCount();

}