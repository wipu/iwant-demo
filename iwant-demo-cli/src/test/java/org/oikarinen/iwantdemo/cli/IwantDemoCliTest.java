package org.oikarinen.iwantdemo.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IwantDemoCliTest {

	private ByteArrayOutputStream outBytes;
	private PrintStream out;

	@BeforeEach
	public void before() {
		outBytes = new ByteArrayOutputStream();
		out = new PrintStream(outBytes);
	}

	@Test
	public void first4Primes() {
		new IwantDemoCli(out).doMain(new String[] { "--primes", "4" });

		assertEquals("2\n" + "3\n" + "5\n" + "7\n" + "",
				new String(outBytes.toByteArray(), StandardCharsets.UTF_8));
	}

	@Test
	public void generatedBean() {
		new IwantDemoCli(out)
				.doMain(new String[] { "--generatedbean", "new value" });

		assertEquals(
				"Using class org.oikarinen.generatedbeans.a.GeneratedBean by setting s=new value\n"
						+ "initial s: null\n" + "s after setting: new value\n"
						+ "",
				new String(outBytes.toByteArray(), StandardCharsets.UTF_8));
	}

}
