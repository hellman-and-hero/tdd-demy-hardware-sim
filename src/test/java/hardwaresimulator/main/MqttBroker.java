package hardwaresimulator.main;

import static io.moquette.broker.config.IConfig.ALLOW_ANONYMOUS_PROPERTY_NAME;
import static io.moquette.broker.config.IConfig.ENABLE_TELEMETRY_NAME;
import static io.moquette.broker.config.IConfig.HOST_PROPERTY_NAME;
import static io.moquette.broker.config.IConfig.PERSISTENCE_ENABLED_PROPERTY_NAME;
import static io.moquette.broker.config.IConfig.PORT_PROPERTY_NAME;
import static java.util.stream.Collectors.toMap;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;

public class MqttBroker implements Closeable {

	public static class Builder {

		private String host = "localhost";
		private Integer port;

		private Builder() {
			super();
		}

		public Builder host(String host) {
			this.host = host;
			return this;
		}

		public Builder port(Integer port) {
			this.port = port;
			return this;
		}

		public MqttBroker startBroker() throws IOException {
			return new MqttBroker(this);
		}

		public Map<String, String> properties() {
			return Map.of( //
					HOST_PROPERTY_NAME, host, //
					PORT_PROPERTY_NAME, portOr(1883), //
					ALLOW_ANONYMOUS_PROPERTY_NAME, true, //
					PERSISTENCE_ENABLED_PROPERTY_NAME, false, //
					ENABLE_TELEMETRY_NAME, false //
			).entrySet().stream().collect(toMap(Map.Entry::getKey, e -> e.getValue().toString()));
		}

		private int portOr(int fallback) {
			return port == null ? fallback : port;
		}

	}

	private final Server broker;
	private final IConfig config;

	public static Builder builder() {
		return new Builder();
	}

	public MqttBroker(Builder builder) throws IOException {
		config = new MemoryConfig(toProperties(builder.properties()));
		broker = startBroker(new Server(), config);
	}

	private static Properties toProperties(Map<? extends Object, ? extends Object> map) {
		Properties properties = new Properties();
		properties.putAll(map);
		return properties;
	}

	private Server startBroker(Server server, IConfig config) throws IOException {
		server.startServer(config, null, null, null, null);
		return server;
	}

	public String host() {
		return config.getProperty(HOST_PROPERTY_NAME);
	}

	public int port() {
		return Integer.parseInt(config.getProperty(PORT_PROPERTY_NAME));
	}

	@Override
	public void close() {
		broker.stopServer();
	}

	public void stop() {
		close();
	}

}
