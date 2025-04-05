package hardwaresimulator.sim;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.util.List;

import org.junit.jupiter.api.Test;

class LedStripTest {

	class RingBuilder {

		int rings;

		RingBuilder(int rings) {
			this.rings = rings;
		}

		void eachOfLeds(int leds) {
			levelMeters = range(0, rings).mapToObj(__ -> levelMeterWithLeds(leds)).collect(toList());
			sut = new LedStrip(levelMeters);
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

	List<LevelMeter> levelMeters;
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

	@Test
	void whenSwitchingOutOfRange_NothingHappens() {
		givenRings(2).eachOfLeds(2);
		sut.switchLed(4, BLUE);
		verifyNoLedSwitched(ring(0));
		verifyNoLedSwitched(ring(1));
	}

	LevelMeter ring(int ringNumber) {
		return levelMeters.get(ringNumber);
	}

	void verifySwitched(LevelMeter levelMeter, int led, Color color) {
		verify(levelMeter).setColor(led, color);
	}

	void verifyNoLedSwitched(LevelMeter levelMeter) {
		verify(levelMeter, never()).setColor(anyInt(), any(Color.class));
	}

}
