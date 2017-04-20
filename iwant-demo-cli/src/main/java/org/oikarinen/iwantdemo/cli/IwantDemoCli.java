package org.oikarinen.iwantdemo.cli;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.oikarinen.iwantdemo.mathlib.Mathlib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IwantDemoCli {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IwantDemoCli.class);

	private final PrintStream out;

	IwantDemoCli(PrintStream out) {
		this.out = out;
	}

	public static void main(String[] args) {
		LOGGER.info("Running " + IwantDemoCli.class + " with args "
				+ Arrays.toString(args));
		new IwantDemoCli(System.out).doMain(args);
	}

	void doMain(String[] args) {
		Options o = new Options();

		Option help = new Option("h", "help", false,
				"display this help message");
		o.addOption(help);

		Option primes = new Option("p", "primes", true,
				"print <count> first prime numbers");
		primes.setArgName("count");
		o.addOption(primes);

		CommandLineParser parser = new DefaultParser();

		CommandLine cmd;
		try {
			cmd = parser.parse(o, args);
		} catch (ParseException e) {
			showHelpAndExit(o);
			return;
		}
		if (cmd.hasOption(help.getOpt())) {
			showHelpAndExit(o);
			return;
		}
		if (cmd.hasOption(primes.getOpt())) {
			int count = Integer.parseInt(cmd.getOptionValue(primes.getOpt()));
			printPrimes(count);
			return;
		}
		showHelpAndExit(o);
		return;
	}

	private void printPrimes(int count) {
		List<Integer> output = Mathlib.firstNPrimes(count);
		for (Integer p : output) {
			out.println(p);
		}
	}

	private void showHelpAndExit(Options o) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(getClass().getSimpleName()
				+ " [OPTIONS]\n  where OPTIONS is one of:", o);
		exit(1);
	}

	protected void exit(int code) {
		System.exit(code);
	}

}
