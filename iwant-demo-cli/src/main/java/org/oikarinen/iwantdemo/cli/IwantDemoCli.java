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
import org.oikarinen.generatedbeans.a.GeneratedBean;
import org.oikarinen.iwantdemo.mathlib.Mathlib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import joulu.unsignedbyte.UnsignedByte;

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

		Option generatedBean = new Option("g", "generatedbean", true,
				"use generated javabean by setting s=<sValue>");
		generatedBean.setArgName("sValue");
		o.addOption(generatedBean);

		Option unsignedByte = new Option("u", "unsigned-byte", true,
				"use joulu-unsigned-byte to convert <int> to"
						+ " unsigned byte and display information");
		unsignedByte.setArgName("int");
		o.addOption(unsignedByte);

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
		if (cmd.hasOption(generatedBean.getOpt())) {
			String v = cmd.getOptionValue(generatedBean.getOpt());
			useGeneratedBean(v);
			return;
		}
		if (cmd.hasOption(unsignedByte.getOpt())) {
			int value = Integer
					.parseInt(cmd.getOptionValue(unsignedByte.getOpt()));
			useJouluUnsignedByte(value);
			return;
		}
		showHelpAndExit(o);
		return;
	}

	private void useJouluUnsignedByte(int value) {
		out.println("Integer " + value + " as unsigned byte 'b'");
		UnsignedByte b = UnsignedByte.from(value);
		out.println("b: " + b);
		out.println("b.not(): " + b.not());
		out.println("b.isBit0(): " + b.isBit0());
	}

	private void useGeneratedBean(String v) {
		out.println("Using " + GeneratedBean.class + " by setting s=" + v);
		GeneratedBean b = new GeneratedBean();
		out.println("initial s: " + b.getS());
		b.setS(v);
		out.println("s after setting: " + b.getS());
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
