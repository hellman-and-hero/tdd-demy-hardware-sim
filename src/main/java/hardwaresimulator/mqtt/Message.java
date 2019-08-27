package hardwaresimulator.mqtt;

public class Message {

	private final String topic;
	private final String payload;
	private final boolean retained;

	protected Message(String topic, Object payload, boolean retained) {
		this.topic = topic;
		this.payload = payload == null ? null : String.valueOf(payload);
		this.retained = retained;
	}

	public String getPayload() {
		return payload;
	}

	public String getTopic() {
		return topic;
	}

	public boolean isRetained() {
		return retained;
	}

}