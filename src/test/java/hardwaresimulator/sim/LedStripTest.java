package hardwaresimulator.sim;

import static hardwaresimulator.sim.Led.led;
import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
		assertThat(sut.switchLed(led(0), RED)).isTrue();
		verifySwitched(ring(0), 0, RED);
		verifyNoLedSwitched(ring(1));
	}

	@Test
	void whenSwitchingLed2_Led0OnRing1IsSwitched() {
		givenRings(2).eachOfLeds(2);
		assertThat(sut.switchLed(led(2), GREEN)).isTrue();
		verifyNoLedSwitched(ring(0));
		verifySwitched(ring(1), 0, GREEN);
	}

	@Test
	void whenSwitchingLed3_Led1OnRing1IsSwitched() {
		givenRings(2).eachOfLeds(2);
		assertThat(sut.switchLed(led(3), BLUE)).isTrue();
		verifyNoLedSwitched(ring(0));
		verifySwitched(ring(1), 1, BLUE);
	}

	@Test
	void whenSwitchingUpperOutOfRange_NothingHappensAndFalseIsReturned() {
		givenRings(2).eachOfLeds(2);
		assertThat(sut.switchLed(led(4), BLUE)).isFalse();
		verifyNoLedSwitched(ring(0));
		verifyNoLedSwitched(ring(1));
	}

	LevelMeter ring(int ringNumber) {
		return levelMeters.get(ringNumber);
	}

	void verifySwitched(LevelMeter levelMeter, int led, Color color) {
		verify(levelMeter).setColor(led(led), color);
	}

	void verifyNoLedSwitched(LevelMeter levelMeter) {
		verify(levelMeter, never()).setColor(any(Led.class), any(Color.class));
	}

}
