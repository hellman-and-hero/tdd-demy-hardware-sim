package hardwaresimulator.main;

import static org.kohsuke.args4j.OptionHandlerFilter.ALL;
import static org.kohsuke.args4j.ParserProperties.defaults;

import java.util.Optional;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import hardwaresimulator.main.args4j.Args4JCmdLineArguments;
import hardwaresimulator.sim.HardwareSimulater;
import hardwaresimulator.sim.HardwareSimulater.Config;

public class Main {

	public static void main(String... args) {
		createConfig(args).ifPresent(HardwareSimulater::new);
	}

	protected static Optional<Config> createConfig(String... args) {
		return tryParseArgs(args).map(Main::argsAdapter);
	}

	private static Optional<Args4JCmdLineArguments> tryParseArgs(String... args) {
		Args4JCmdLineArguments args4jArguments = new Args4JCmdLineArguments();
		CmdLineParser parser = new CmdLineParser(args4jArguments, defaults().withUsageWidth(80));
		try {
			parser.parseArgument(args);
			if (!args4jArguments.help) {
				return Optional.of(args4jArguments);
			}
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
		}
		printHelp(parser);
		return Optional.empty();
	}

	private static void printHelp(CmdLineParser parser) {
		String mainClassName = Main.class.getName();
		System.err.println("java " + mainClassName + " [options...] arguments...");
		parser.printUsage(System.err);
		System.err.println();
		System.err.println("  Example: java " + mainClassName + parser.printExample(ALL));
	}

	private static Config argsAdapter(Args4JCmdLineArguments args) {
		record ConfigAdapter(String mqttHost, int mqttPort, int rings, int ledCount, int ringSize, int ledSize)
				implements Config {
		}
		return new ConfigAdapter(args.mqttHost, args.mqttPort, args.rings, args.ledCount, args.ringSize, args.ledSize);
	}

}
