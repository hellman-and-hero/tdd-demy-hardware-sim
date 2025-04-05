package hardwaresimulator.sim;

import static hardwaresimulator.sim.Led.led;
import static java.awt.Color.BLACK;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;

import java.awt.Color;
import java.util.function.Function;

import org.approvaltests.awt.AwtApprovals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

class LevelMeterApprovalTest {

	private static final String IS_HEADLESS = "java.awt.GraphicsEnvironment#isHeadless";

	JLevelMeter sut = new JLevelMeter(16);
	Color[] colors = new Color[] { GREEN, YELLOW, ORANGE, RED };

	@Test
	@DisabledIf(IS_HEADLESS)
	void noInteraction() {
		AwtApprovals.verify(sut);
	}

	@Test
	@DisabledIf(IS_HEADLESS)
	void someColors() {
		colorRing(sut.getLedCount() - 2, this::zoneColor);
		AwtApprovals.verify(sut);
	}

	@Test
	@DisabledIf(IS_HEADLESS)
	void checkBackgroundColorRepaintedCorrectly() {
		colorRing(sut.getLedCount(), this::zoneColor);
		colorRing(sut.getLedCount(), __ -> BLACK);
		AwtApprovals.verify(sut);
	}

	private void colorRing(int max, Function<Led, Color> colorProvider) {
		for (int i = 0; i < max; i++) {
			Led led = led(i);
			sut.setColor(led, colorProvider.apply(led));
		}
	}

	private Color zoneColor(Led led) {
		return colors[zone(led)];
	}

	private int zone(Led led) {
		int zone = colors.length - 1;
		while (zone > 0 && led.index() <= sut.getLedCount() * zone / colors.length) {
			zone--;
		}
		return zone;
	}

}
