package hardwaresimulator.main.args4j;

import org.kohsuke.args4j.Option;

import hardwaresimulator.sim.JLevelMeter;

public class Args4JCmdLineArguments {

	@Option(name = "-h", help = true)
	public boolean help;

	@Option(name = "-mqttHost", usage = "hostname of the mqtt broker")
	public String mqttHost = "127.0.0.1";
	@Option(name = "-mqttPort", usage = "port of the mqtt broker")
	public int mqttPort = 1883;

	@Option(name = "-rings", usage = "how many leds rings should be simulated")
	public int rings = 2;
	@Option(name = "-ledCount", usage = "how many leds should each ring have")
	public int ledCount = 16;
	@Option(name = "-ringSize", usage = "size in pixels of each ring")
	public int ringSize = JLevelMeter.DEFAULT_RING_SIZE;
	@Option(name = "-ledSize", usage = "size in pixels of each led")
	public int ledSize = JLevelMeter.DEFAULT_LED_SIZE;

}