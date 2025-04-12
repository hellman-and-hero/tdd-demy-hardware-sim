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

import hardwaresimulator.main.Main.ConfigAdapter;
import hardwaresimulator.sim.MqttLedStripService.Config;

class MqttLedStripServiceIT {

	private MqttBroker broker;
	private MqttClient sender;
	private Config config;
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
		config = new ConfigAdapter(broker.host(), broker.port(), 2, 4, 0, 0);
		levelMeters = range(0, config.rings()).mapToObj(__ -> levelMeterMock(config)).toList();
		MqttLedStripService service = new MqttLedStripService(config, new LedStrip(levelMeters));
		await().until(service::isConnected);
		return service;
	}

	private static LevelMeter levelMeterMock(Config config) {
		LevelMeter mock = mock(LevelMeter.class);
		when(mock.getLedCount()).thenReturn(config.ledCount());
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
		publishMessage(led(ringOffset(0) + 1), "#1122FF");
		publishMessage(led(ringOffset(1) + 3), "#FFFFFF");
		await().untilAsserted(() -> {
			verify(levelMeters.get(0)).setColor(led(1), new Color(17, 34, 255));
			verify(levelMeters.get(1)).setColor(led(3), new Color(255, 255, 255));
		});
	}

	private void publishMessage(Led led, String string) throws Exception {
		sender.publish(message(led), new MqttMessage(string.getBytes()));
	}

	private int ringOffset(int ring) {
		return ring * config.ledCount();
	}

	private static String message(Led led) {
		return format("some/led/%d/rgb", led.index());
	}

}
