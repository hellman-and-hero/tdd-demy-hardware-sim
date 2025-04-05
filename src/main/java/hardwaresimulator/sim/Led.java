package hardwaresimulator.sim;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

// turn into record with java 17+
public class Led {

	public static Led fromString(String index) {
		return led(parseInt(index));
	}

	public static Led led(int index) {
		return new Led(index);
	}

	private final int index;

	private Led(int index) {
		if (index < 0) {
			throw new IllegalArgumentException(format("index must not be negative but was %d", index));
		}
		this.index = index;
	}

	public int index() {
		return index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Led other = (Led) obj;
		if (index != other.index)
			return false;
		return true;
	}

}
