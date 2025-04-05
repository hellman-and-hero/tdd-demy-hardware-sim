package hardwaresimulator.sim;

import static hardwaresimulator.sim.Led.led;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class LedTest {

	@Test
	void cannotCreateLedsWithIndex0() {
		int index = 0;
		assertThat(led(index).index()).isZero();
		assertThat(Led.fromString(String.valueOf(index)).index()).isZero();
	}

	@Test
	void cannotCreateLedsWithNegativeIndex() {
		int index = -1;
		assertThrows(IllegalArgumentException.class, () -> led(index));
		assertThrows(IllegalArgumentException.class, () -> Led.fromString(String.valueOf(index)));
	}

}
