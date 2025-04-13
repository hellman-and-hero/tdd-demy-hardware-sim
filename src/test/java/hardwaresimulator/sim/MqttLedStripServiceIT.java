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

import org.junit.jupiter.api.AfterEach;
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

	@Test
	void consumeMqttMessage() throws Exception {
		Color color1 = Color.decode("#FF0000");
		Color color2 = Color.decode("#00FF00");
		Color color3 = Color.decode("#0000FF");
		publishMessage(0, led(1), color1);
		publishMessage(1, led(0), color2);
		publishMessage(1, led(3), color3);
		await().untilAsserted(() -> {
			verify(levelMeters.get(0)).setColor(led(1), color1);
			verify(levelMeters.get(1)).setColor(led(0), color2);
			verify(levelMeters.get(1)).setColor(led(3), color3);
		});
	}

	private void publishMessage(int ring, Led led, Color color) throws IOException {
		sender.publish(message(stripIndex(ring, led)), toString(color));
	}

	private int stripIndex(int ring, Led led) {
		return ringOffset(ring) + led.index();
	}

	private static String message(int stripIndex) {
		return format("some/led/%d/rgb", stripIndex);
	}

	private static String toString(Color color) {
		return format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private int ringOffset(int ring) {
		return ring * LED_COUNT;
	}

}
