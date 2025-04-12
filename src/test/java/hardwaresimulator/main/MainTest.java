package hardwaresimulator.main;

import static com.github.stefanbirkner.systemlambda.SystemLambda.assertNothingWrittenToSystemErr;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static hardwaresimulator.main.MainTest.Params.LED_COUNT;
import static hardwaresimulator.main.MainTest.Params.LED_RINGS;
import static hardwaresimulator.main.MainTest.Params.LED_SIZE;
import static hardwaresimulator.main.MainTest.Params.MQTT_HOST;
import static hardwaresimulator.main.MainTest.Params.MQTT_PORT;
import static hardwaresimulator.main.MainTest.Params.RING_SIZE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

		private final String value;

		private Params(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public static List<String> all() {
			return Arrays.stream(values()).map(Params::getValue).toList();
		}
	}

	@Test
	void printsHelpForEachOptionOnMinusH() throws Exception {
		assertThat(tapSystemErr(() -> Main.main("-h"))).contains(Params.all());
	}

	@Test
	void canSetAllOptions() throws Exception {
		Config expectedConfig = new ConfigAdapter("a", 1, 2, 3, 4, 5);
		List<String> args = Stream.of( //
				param(MQTT_HOST), expectedConfig.mqttHost(), //
				param(MQTT_PORT), expectedConfig.mqttPort(), //
				param(LED_RINGS), expectedConfig.rings(), //
				param(RING_SIZE), expectedConfig.ringSize(), //
				param(LED_COUNT), expectedConfig.ledCount(), //
				param(LED_SIZE), expectedConfig.ledSize() //
		).map(String::valueOf).toList();
		assertNothingWrittenToSystemErr(() -> assertThat(parseArgs(args)).isEqualTo(expectedConfig));
	}

	private static String param(Params params) {
		return params.value;
	}

	private static Config parseArgs(List<String> args) {
		return Main.createConfig(args.toArray(String[]::new)).orElse(null);
	}

}
