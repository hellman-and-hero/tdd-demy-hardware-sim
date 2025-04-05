package hardwaresimulator.sim;

import static hardwaresimulator.sim.Led.led;
import static java.awt.Color.BLACK;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;

import java.awt.Color;

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
		for (int i = 0; i < sut.getLedCount() - 2; i++) {
			Led led = led(i);
			sut.setColor(led, color(led));
		}
		AwtApprovals.verify(sut);
	}

	@Test
	@DisabledIf(IS_HEADLESS)
	void checkBackgroundColorRepaintedCorrectly() {
		for (int i = 0; i < sut.getLedCount(); i++) {
			Led led = led(i);
			sut.setColor(led, color(led));
		}

		for (int i = 0; i < sut.getLedCount(); i++) {
			sut.setColor(led(i), BLACK);
		}
		AwtApprovals.verify(sut);
	}

	private Color color(Led led) {
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
