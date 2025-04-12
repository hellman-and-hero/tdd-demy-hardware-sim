package hardwaresimulator.main;

import static com.github.stefanbirkner.systemlambda.SystemLambda.assertNothingWrittenToSystemErr;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static hardwaresimulator.main.MainTest.Params.LED_COUNT;
import static hardwaresimulator.main.MainTest.Params.LED_RINGS;
import static hardwaresimulator.main.MainTest.Params.LED_SIZE;
import static hardwaresimulator.main.MainTest.Params.MQTT_HOST;
import static hardwaresimulator.main.MainTest.Params.MQTT_PORT;
import static hardwaresimulator.main.MainTest.Params.RING_SIZE;
import static hardwaresimulator.main.MainTest.Params.args;
import static hardwaresimulator.main.MainTest.Params.options;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import hardwaresimulator.main.Main.ConfigAdapter;
import hardwaresimulator.sim.MqttLedStripService.Config;

class MainTest {

	public static enum Params {
		MQTT_HOST("-mqttHost"), //
		MQTT_PORT("-mqttPort"), //
		LED_RINGS("-rings"), //
		RING_SIZE("-ringSize"), //
		LED_COUNT("-ledCount"), //
		LED_SIZE("-ledSize"); //

		private final String option;

		private Params(String option) {
			this.option = option;
		}

		public String getOption() {
			return option;
		}

		public static List<String> options() {
			return stream(values()).map(Params::getOption).toList();
		}

		public List<String> withValue(Object value) {
			return List.of(getOption(), String.valueOf(value));
		}

		@SafeVarargs
		public static String[] args(List<String>... args) {
			return stream(args).flatMap(List::stream).toArray(String[]::new);
		}

	}

	@Test
	void printsHelpForEachOptionOnMinusH() throws Exception {
		assertThat(tapSystemErr(() -> Main.main("-h"))).contains(options());
	}

	@Test
	void canSetAllOptions() throws Exception {
		Config config = new ConfigAdapter("a", 1, 2, 3, 4, 5);

		String[] args = args( //
				MQTT_HOST.withValue(config.mqttHost()), //
				MQTT_PORT.withValue(config.mqttPort()), //
				LED_RINGS.withValue(config.rings()), //
				RING_SIZE.withValue(config.ringSize()), //
				LED_COUNT.withValue(config.ledCount()), //
				LED_SIZE.withValue(config.ledSize()) //
		);
		assertNothingWrittenToSystemErr(() -> assertThat(parseArgs(args)).isEqualTo(config));
	}

	private static Config parseArgs(String[] args) {
		AtomicReference<Config> ref = new AtomicReference<>();
		Main main = new Main() {
			@Override
			protected void showGui(Config config) {
				ref.set(config);
			}
		};
		main.exec(args);
		return ref.get();
	}

}
