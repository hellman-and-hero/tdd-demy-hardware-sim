package hardwaresimulator;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class LevelMetersTest {

	List<LevelMeter> levelMeters = new ArrayList<>();
	LevelMeters sut = new LevelMeters(2, () -> createSpy(levelMeters));

	@Test
	void whenSwitchingLed0_Led0OnRing0IsSwitched() {
		sut.switchLed(0, RED);
		verifySwitched(ring(0), 0, RED);
		verifyNoLedSwitched(ring(1));
	}

	private static LevelMeter createSpy(List<LevelMeter> spies) {
		LevelMeter spy = spy(new LevelMeter(2));
		spies.add(spy);
		return spy;
	}

	@Test
	void whenSwitchingLed2_Led0OnRing1IsSwitched() {
		sut.switchLed(2, GREEN);
		verifyNoLedSwitched(ring(0));
		verifySwitched(ring(1), 0, GREEN);
	}

	@Test
	void whenSwitchingLed3_Led1OnRing1IsSwitched() {
		sut.switchLed(3, BLUE);
		verifyNoLedSwitched(ring(0));
		verifySwitched(ring(1), 1, BLUE);
	}

	LevelMeter ring(int ring) {
		return levelMeters.get(ring);
	}

	void verifySwitched(LevelMeter levelMeter, int led, Color color) {
		verify(levelMeter).setColor(led, color);
	}

	void verifyNoLedSwitched(LevelMeter levelMeter) {
		verify(levelMeter, times(0)).setColor(anyInt(), any(Color.class));
	}

}
