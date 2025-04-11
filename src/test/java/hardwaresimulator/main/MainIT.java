package hardwaresimulator.main;

import static hardwaresimulator.sim.Led.led;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.io.IOException;
import java.net.ServerSocket;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hardwaresimulator.main.Main.ConfigAdapter;
import hardwaresimulator.sim.HardwareSimulater;
import hardwaresimulator.sim.JLevelMeter;
import hardwaresimulator.sim.Led;

class MainIT {

	private static final int LED_COUNT = 4;
	private MqttBroker broker;
	private MqttClient sender;
	private HardwareSimulater sut;

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
		broker.close();
	}

	private static int freePort() throws IOException {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		}
	}

	private static HardwareSimulater createSut(MqttBroker broker) {
		ConfigAdapter config = new ConfigAdapter(broker.host(), broker.port(), 2, LED_COUNT, 0, 0);
		HardwareSimulater hardwareSimulater = new HardwareSimulater(config) {
			@Override
			protected JLevelMeter newLevelMeter(Config config) {
				JLevelMeter mock = spy(new JLevelMeter(config.ledCount()) {

					private static final long serialVersionUID = 1L;

					@Override
					public void setColor(Led led, Color color) {
						// no-op
					}
				});
				return mock;
			}
		};
		await().until(hardwareSimulater::isConnected);
		return hardwareSimulater;
	}

	private static MqttClient sender(MqttBroker broker) throws MqttException, MqttSecurityException {
		MqttClient sender = new MqttClient(format("tcp://%s:%d", broker.host(), broker.port()),
				format("%s-%d", MainIT.class.getName(), currentTimeMillis()), new MemoryPersistence());
		sender.connect();
		await().until(sender::isConnected);
		return sender;
	}

	@Test
	void consumeMqttMessage() throws Exception {
		publishMessage(led(ringOffset(0) + 1), "#1122FF");
		publishMessage(led(ringOffset(1) + 3), "#FFFFFF");
		await().untilAsserted(() -> {
			verify(sut.levelMeters(0)).setColor(led(1), new Color(17, 34, 255));
			verify(sut.levelMeters(1)).setColor(led(3), new Color(255, 255, 255));
		});
	}

	private void publishMessage(Led led, String string) throws Exception {
		sender.publish(message(led), new MqttMessage(string.getBytes()));
	}

	private static int ringOffset(int ring) {
		return ring * LED_COUNT;
	}

	private static String message(Led led) {
		return format("some/led/%d/rgb", led.index());
	}

}
