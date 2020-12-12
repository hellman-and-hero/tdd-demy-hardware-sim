package hardwaresimulator;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MainTest {

	@Test
	void printsHelpForEachOptionOnMinusH() throws Exception {
		assertThat(tapSystemErr(() -> Main.main("-h"))) //
				.contains("-mqttHost ") //
				.contains("-mqttPort ") //
				.contains("-rings ") //
				.contains("-ledCount ") //
				.contains("-ledSize ");
	}

}
