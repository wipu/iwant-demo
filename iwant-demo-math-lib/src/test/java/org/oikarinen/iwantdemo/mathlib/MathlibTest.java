package org.oikarinen.iwantdemo.mathlib;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

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
		assertEquals(Arrays.asList(), Mathlib.firstNPrimes(0));
	}

	@Test
	public void firstOnePrimes() {
		assertEquals(Arrays.asList(2), Mathlib.firstNPrimes(1));
	}

	@Test
	public void firstThreePrimes() {
		assertEquals(Arrays.asList(2, 3, 5), Mathlib.firstNPrimes(3));
	}

}
