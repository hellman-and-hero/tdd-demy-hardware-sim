package hardwaresimulator.mqtt;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttConsumer implements Closeable {

	private final MqttClient mqttClient;
	private final List<Consumer<Message>> consumers = new CopyOnWriteArrayList<>();

	public MqttConsumer(String host, int port, String topic) throws IOException {
		try {
			mqttClient = new MqttClient("tcp://" + host + ":" + port,
					getClass().getName() + "-" + System.currentTimeMillis(), new MemoryPersistence());
			mqttClient.setTimeToWait(SECONDS.toMillis(1));
			mqttClient.setCallback(callback(() -> subscribe(topic)));
			mqttClient.connect(connectOptions());
		} catch (MqttException e) {
			throw new IOException(e);
		}
	}

	@FunctionalInterface
	public interface OnConnect {
		void onConnect() throws Exception;
	}

	private MqttCallback callback(OnConnect onConnect) {
		return new MqttCallbackExtended() {
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				messageArrived(convert(topic, message));
			}

			private void messageArrived(Message message) {
				consumers.forEach(consumer -> consumer.accept(message));
			}

			private Message convert(String topic, MqttMessage mqttMessage) {
				return new Message(topic, new String(mqttMessage.getPayload()), false);
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
			}

			@Override
			public void connectionLost(Throwable cause) {
			}

			@Override
			public void connectComplete(boolean reconnect, String serverURI) {
				try {
					onConnect.onConnect();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private MqttConnectOptions connectOptions() {
		MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
		mqttConnectOptions.setAutomaticReconnect(true);
		return mqttConnectOptions;
	}

	private void subscribe(String topic) throws MqttException {
		mqttClient.subscribe(topic);
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

	public void addConsumer(Consumer<Message> consumer) {
		consumers.add(consumer);
	}

}
