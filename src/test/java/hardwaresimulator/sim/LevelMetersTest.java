package hardwaresimulator.sim;

import static hardwaresimulator.sim.LevelMetersTest.RingBuilder.rings;
import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.util.List;

import org.junit.jupiter.api.Test;

class LevelMetersTest {

	static class RingBuilder {

		private int rings;

		public RingBuilder(int rings) {
			this.rings = rings;
		}

		public static RingBuilder rings(int rings) {
			return new RingBuilder(rings);
		}

		public List<LevelMeter> withLeds(int leds) {
			return range(0, rings).mapToObj(i -> levelMeterWithLeds(leds)).collect(toList());
		}

		private static LevelMeter levelMeterWithLeds(int leds) {
			LevelMeter mock = mock(LevelMeter.class);
			when(mock.getLedCount()).thenReturn(leds);
			return mock;
		}

	}

	List<LevelMeter> levelMeters;
	LedStrip sut;

	@Test
	void whenSwitchingLed0_Led0OnRing0IsSwitched() {
		sut = createSut(rings(2).withLeds(2));
		sut.switchLed(0, RED);
		verifySwitched(ring(0), 0, RED);
		verifyNoLedSwitched(ring(1));
	}

	@Test
	void whenSwitchingLed2_Led0OnRing1IsSwitched() {
		sut = createSut(rings(2).withLeds(2));
		sut.switchLed(2, GREEN);
		verifyNoLedSwitched(ring(0));
		verifySwitched(ring(1), 0, GREEN);
	}

	@Test
	void whenSwitchingLed3_Led1OnRing1IsSwitched() {
		sut = createSut(rings(2).withLeds(2));
		sut.switchLed(3, BLUE);
		verifyNoLedSwitched(ring(0));
		verifySwitched(ring(1), 1, BLUE);
	}

	LedStrip createSut(List<LevelMeter> levelMeters) {
		this.levelMeters = levelMeters;
		return new LedStrip(levelMeters);
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
