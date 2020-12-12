package hardwaresimulator;

import static org.kohsuke.args4j.OptionHandlerFilter.ALL;
import static org.kohsuke.args4j.ParserProperties.defaults;

import java.io.IOException;
import java.util.Optional;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {

	public static void main(String... args) throws IOException {
		tryParseArgs(args).ifPresent(HardwareSimulater::new);
	}

	private static Optional<CommandLineArguments> tryParseArgs(String... args) {
		CommandLineArguments cmdLineArgs = new CommandLineArguments();
		CmdLineParser parser = new CmdLineParser(cmdLineArgs, defaults().withUsageWidth(80));
		try {
			parser.parseArgument(args);
			if (!cmdLineArgs.help) {
				return Optional.of(cmdLineArgs);
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

}
