package hardwaresimulator.main;

import static com.github.stefanbirkner.systemlambda.SystemLambda.assertNothingWrittenToSystemErr;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.Test;

import hardwaresimulator.sim.HardwareSimulater.Config;

class MainTest {

	private static final String MQTT_HOST = "-mqttHost";
	private static final String MQTT_PORT = "-mqttPort";
	private static final String LED_RINGS = "-rings";
	private static final String RING_SIZE = "-ringSize";
	private static final String LED_COUNT = "-ledCount";
	private static final String LED_SIZE = "-ledSize";

	@Test
	void printsHelpForEachOptionOnMinusH() throws Exception {
		assertThat(tapSystemErr(() -> Main.main("-h"))) //
				.contains(MQTT_HOST) //
				.contains(MQTT_PORT) //
				.contains(LED_RINGS) //
				.contains(RING_SIZE) //
				.contains(LED_COUNT) //
				.contains(LED_SIZE);
	}

	@Test
	void canSetAllOptions() throws Exception {
		List<String> args = asList( //
				MQTT_HOST, "a", //
				MQTT_PORT, "1", //
				LED_RINGS, "2", //
				RING_SIZE, "3", //
				LED_COUNT, "4", //
				LED_SIZE, "5" //
		);

		assertNothingWrittenToSystemErr(() -> {
			Config config = execSut(args);
			assertAll( //
					() -> assertThat(config.mqttHost()).isEqualTo("a"), //
					() -> assertThat(config.mqttPort()).isEqualTo(1), //
					() -> assertThat(config.rings()).isEqualTo(2), //
					() -> assertThat(config.ringSize()).isEqualTo(3), //
					() -> assertThat(config.ledCount()).isEqualTo(4), //
					() -> assertThat(config.ledSize()).isEqualTo(5) //
			);
		});

	}

	private Config execSut(List<String> args) {
		return Main.createConfig(args.toArray(new String[0])).orElse(null);
	}

}
