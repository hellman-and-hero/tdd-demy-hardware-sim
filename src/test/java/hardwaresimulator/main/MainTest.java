package hardwaresimulator.main;

import static com.github.stefanbirkner.systemlambda.SystemLambda.assertNothingWrittenToSystemErr;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static hardwaresimulator.main.MainTest.Params.LED_COUNT;
import static hardwaresimulator.main.MainTest.Params.LED_RINGS;
import static hardwaresimulator.main.MainTest.Params.LED_SIZE;
import static hardwaresimulator.main.MainTest.Params.MQTT_HOST;
import static hardwaresimulator.main.MainTest.Params.MQTT_PORT;
import static hardwaresimulator.main.MainTest.Params.RING_SIZE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import hardwaresimulator.sim.HardwareSimulater.Config;

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
		List<String> args = asList( //
				param(MQTT_HOST), "a", //
				param(MQTT_PORT), "1", //
				param(LED_RINGS), "2", //
				param(RING_SIZE), "3", //
				param(LED_COUNT), "4", //
				param(LED_SIZE), "5" //
		);

		assertNothingWrittenToSystemErr(() -> {
			Config config = parseArgs(args);
			assertSoftly(s -> {
				s.assertThat(config.mqttHost()).isEqualTo("a");
				s.assertThat(config.mqttPort()).isEqualTo(1);
				s.assertThat(config.rings()).isEqualTo(2);
				s.assertThat(config.ringSize()).isEqualTo(3);
				s.assertThat(config.ledCount()).isEqualTo(4);
				s.assertThat(config.ledSize()).isEqualTo(5);
			});
		});

	}

	private static String param(Params params) {
		return params.value;
	}

	private static Config parseArgs(List<String> args) {
		return Main.createConfig(args.toArray(String[]::new)).orElse(null);
	}

}
