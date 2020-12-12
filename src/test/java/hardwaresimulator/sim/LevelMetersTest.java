package hardwaresimulator.sim;

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
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class LevelMetersTest {

	class RingBuilder {

		int rings;

		RingBuilder(int rings) {
			this.rings = rings;
		}

		void eachOfLeds(int leds) {
			sut = new LedStrip(range(0, rings).mapToObj(i -> levelMeterWithLeds(leds)).collect(toList()));
		}

		LevelMeter levelMeterWithLeds(int leds) {
			LevelMeter mock = mock(LevelMeter.class);
			when(mock.getLedCount()).thenReturn(leds);
			return mock;
		}

	}

	public RingBuilder givenRings(int rings) {
		return new RingBuilder(rings);
	}

	LedStrip sut;

	@Test
	void whenSwitchingLed0_Led0OnRing0IsSwitched() {
		givenRings(2).eachOfLeds(2);
		sut.switchLed(0, RED);
		verifySwitched(ring(0), 0, RED);
		verifyNoLedSwitched(ring(1));
	}

	@Test
	void whenSwitchingLed2_Led0OnRing1IsSwitched() {
		givenRings(2).eachOfLeds(2);
		sut.switchLed(2, GREEN);
		verifyNoLedSwitched(ring(0));
		verifySwitched(ring(1), 0, GREEN);
	}

	@Test
	void whenSwitchingLed3_Led1OnRing1IsSwitched() {
		givenRings(2).eachOfLeds(2);
		sut.switchLed(3, BLUE);
		verifyNoLedSwitched(ring(0));
		verifySwitched(ring(1), 1, BLUE);
	}

	LevelMeter ring(int ringNumber) {
		return skip(sut.iterator(), ringNumber);
	}

	private static <T> T skip(Iterator<T> iterator, int count) {
		for (int i = 0; i < count; i++) {
			iterator.next();
		}
		return iterator.next();
	}

	void verifySwitched(LevelMeter levelMeter, int led, Color color) {
		verify(levelMeter).setColor(led, color);
	}

	void verifyNoLedSwitched(LevelMeter levelMeter) {
		verify(levelMeter, times(0)).setColor(anyInt(), any(Color.class));
	}

}
