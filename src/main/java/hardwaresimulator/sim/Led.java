package hardwaresimulator.sim;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

public record Led(int index) {

	public static Led fromString(String index) {
		return led(parseInt(index));
	}

	public static Led led(int index) {
		return new Led(index);
	}

	public Led {
		if (index < 0) {
			throw new IllegalArgumentException(format("index must not be negative but was %d", index));
		}
	}

}
