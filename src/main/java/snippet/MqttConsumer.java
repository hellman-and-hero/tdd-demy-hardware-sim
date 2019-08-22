package snippet;

import static java.util.concurrent.TimeUnit.SECONDS;
import hardwaresimulator.mqtt.Message;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttConsumer implements Consumer<Message>, Closeable {

	private final MqttClient mqttClient;

	public MqttConsumer(String host, int port) throws IOException {
		try {
			mqttClient = new MqttClient("tcp://" + host + ":" + port,
					getClass().getName(), new MemoryPersistence());
			mqttClient.setTimeToWait(SECONDS.toMillis(1));
			mqttClient.connect(connectOptions());
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	private MqttConnectOptions connectOptions() {
		MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
		mqttConnectOptions.setAutomaticReconnect(true);
		return mqttConnectOptions;
	}

	@Override
	public void accept(Message message) {
		try {
			MqttMessage mqttMessage = new MqttMessage(message.getPayload()
					.getBytes());
			mqttMessage.setRetained(message.isRetained());
			mqttClient.publish(message.getTopic(), mqttMessage);
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			if (mqttClient.isConnected()) {
				mqttClient.disconnect();
			}
			mqttClient.close();
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	public boolean isConnected() {
		return mqttClient.isConnected();
	}

}