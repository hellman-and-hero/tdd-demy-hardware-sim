package hardwaresimulator.sim;

import static hardwaresimulator.sim.Led.led;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.IntStream.range;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MqttLedStripServiceIT {

	private static final int LED_COUNT = 4;

	private MqttBroker broker;
	private MqttClient sender;
	private List<LevelMeter> levelMeters;
	private MqttLedStripService sut;

	@BeforeEach
	void setup() throws Exception {
		broker = MqttBroker.builder().host("localhost").port(freePort()).startBroker();
		sut = createSut(broker);
		sender = sender(broker);
	}

	@AfterEach
	void tearDown() throws Exception {
		sender.disconnect();
		sender.close();
		sut.close();
		broker.close();
	}

	private static int freePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		}
	}

	private MqttLedStripService createSut(MqttBroker broker) throws IOException {
		levelMeters = range(0, 2).mapToObj(__ -> levelMeterMock(LED_COUNT)).toList();
		MqttLedStripService service = new MqttLedStripService(broker.host(), broker.port(), new LedStrip(levelMeters));
		await().until(service::isConnected);
		return service;
	}

	private static LevelMeter levelMeterMock(int ledCount) {
		LevelMeter mock = mock(LevelMeter.class);
		when(mock.getLedCount()).thenReturn(ledCount);
		return mock;
	}

	private static MqttClient sender(MqttBroker broker) throws MqttException, MqttSecurityException {
		MqttClient sender = new MqttClient(format("tcp://%s:%d", broker.host(), broker.port()),
				format("%s-%d", MqttLedStripServiceIT.class.getName(), currentTimeMillis()), new MemoryPersistence());
		sender.connect();
		await().until(sender::isConnected);
		return sender;
	}

	@Test
	void consumeMqttMessage() throws Exception {
		String color1 = "#1122FF";
		String color2 = "#FFFFFF";
		publishMessage(led(ringOffset(0) + 1), color1);
		publishMessage(led(ringOffset(1) + 3), color2);
		await().untilAsserted(() -> {
			verify(levelMeters.get(0)).setColor(led(1), Color.decode(color1));
			verify(levelMeters.get(1)).setColor(led(3), Color.decode(color2));
		});
	}

	private void publishMessage(Led led, String string) throws Exception {
		sender.publish(message(led), new MqttMessage(string.getBytes()));
	}

	private int ringOffset(int ring) {
		return ring * LED_COUNT;
	}

	private static String message(Led led) {
		return format("some/led/%d/rgb", led.index());
	}

}
