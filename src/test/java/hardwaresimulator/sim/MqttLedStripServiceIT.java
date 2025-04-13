package hardwaresimulator.sim;

import static hardwaresimulator.sim.Led.led;
import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hardwaresimulator.mqtt.MqttClient;

class MqttLedStripServiceIT {

	private static final int LED_COUNT = 4;

	private final List<LevelMeter> levelMeters = range(0, 2).mapToObj(__ -> levelMeterMock(LED_COUNT)).toList();

	@AutoClose
	private MqttClient sender;
	@AutoClose
	private MqttLedStripService sut;
	@AutoClose
	private MqttBroker broker;

	@BeforeEach
	void setup() throws Exception {
		broker = MqttBroker.builder().host("localhost").port(freePort()).startBroker();
		sut = createSut(broker);
		sender = sender(broker);
	}

	private static int freePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		}
	}

	private MqttLedStripService createSut(MqttBroker broker) throws IOException {
		return new MqttLedStripService(broker.host(), broker.port(), levelMeters);
	}

	private static LevelMeter levelMeterMock(int ledCount) {
		LevelMeter mock = mock(LevelMeter.class);
		when(mock.getLedCount()).thenReturn(ledCount);
		return mock;
	}

	private static MqttClient sender(MqttBroker broker) throws IOException {
		return new MqttClient(broker.host(), broker.port(), "#");
	}

	private static record LedSwitch(int ring, Led led, Color color) {

		public LedSwitch(int ring, Led led, String color) {
			this(ring, led, Color.decode(color));
		}

		private int stripIndex() {
			return ringOffset(ring) + led.index();
		}

		private static int ringOffset(int ring) {
			return ring * LED_COUNT;
		}

		private String colorHexString() {
			return format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		}

	}

	@Test
	void consumeMqttMessage() {
		List<LedSwitch> messages = List.of( //
				new LedSwitch(0, led(1), "#FF0000"), //
				new LedSwitch(1, led(0), "#00FF00"), //
				new LedSwitch(1, led(3), "#0000FF") //
		);
		messages.forEach(this::publishMessage);
		await().untilAsserted(() -> messages.forEach(m -> verify(levelMeters.get(m.ring)).setColor(m.led, m.color)));
	}

	private void publishMessage(LedSwitch message) {
		try {
			sender.publish(format("some/led/%d/rgb", message.stripIndex()), message.colorHexString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
