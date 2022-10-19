package org.oikarinen.iwantdemo.mathlib;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.primes.Primes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mathlib {

	private static final Logger LOG = LoggerFactory.getLogger(Mathlib.class);

	public static List<Integer> firstNPrimes(int count) {
		LOG.debug("Calculating first {} primes", count);
		List<Integer> primes = new ArrayList<>(count);
		int prime = 2;
		for (int i = 0; i < count; i++) {
			primes.add(prime);
			prime = Primes.nextPrime(prime + 1);
		}
		return primes;
	}

}
