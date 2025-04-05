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

	@Test
	@DisabledIf(IS_HEADLESS)
	void noInteraction() {
		AwtApprovals.verify(sut);
	}

	@Test
	@DisabledIf(IS_HEADLESS)
	void someColors() {
		for (int i = 0; i < sut.getLedCount() - 2; i++) {
			sut.setColor(led(i), color(i));
		}
		AwtApprovals.verify(sut);
	}

	@Test
	@DisabledIf(IS_HEADLESS)
	void checkBackgroundColorRepaintedCorrectly() {
		for (int i = 0; i < sut.getLedCount(); i++) {
			sut.setColor(led(i), color(i));
		}

		for (int i = 0; i < sut.getLedCount(); i++) {
			sut.setColor(led(i), BLACK);
		}
		AwtApprovals.verify(sut);
	}

	private Color color(int ledNumber) {
		if (ledNumber > sut.getLedCount() * 3 / 4) {
			return RED;
		} else if (ledNumber > sut.getLedCount() * 2 / 4) {
			return ORANGE;
		} else if (ledNumber > sut.getLedCount() * 1 / 4) {
			return YELLOW;
		} else {
			return GREEN;
		}
	}

}
