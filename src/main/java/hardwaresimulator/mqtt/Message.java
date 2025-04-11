package hardwaresimulator.mqtt;

public record Message(String topic, String payload, boolean retained) {

	public Message(String topic, Object payload, boolean retained) {
		this(topic, payload == null ? null : String.valueOf(payload), retained);
	}

}