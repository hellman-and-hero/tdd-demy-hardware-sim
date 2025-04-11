package hardwaresimulator.mqtt;

public record Message(String topic, String payload, boolean retained) {
}