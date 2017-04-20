package org.oikarinen.iwantdemo.mathlib;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

public class MathlibTest {

	@Test
	public void firstMinusOnePrimes() {
		try {
			Mathlib.firstNPrimes(-1);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Illegal Capacity: -1", e.getMessage());
		}
	}

	@Test
	public void firstZeroPrimes() {
		assertThat(Mathlib.firstNPrimes(0), is(Arrays.asList()));
	}

	@Test
	public void firstOnePrimes() {
		assertThat(Mathlib.firstNPrimes(1), is(Arrays.asList(2)));
	}

	@Test
	public void firstThreePrimes() {
		assertThat(Mathlib.firstNPrimes(3), is(Arrays.asList(2, 3, 5)));
	}

}
