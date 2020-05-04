package net.jqwik.engine;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import net.jqwik.api.*;

public class SourceOfRandomness {

	private SourceOfRandomness() {
	}

	private static final Supplier<Random> RNG = ThreadLocalRandom::current;

	private static final ThreadLocal<Random> current = ThreadLocal.withInitial(SourceOfRandomness::newRandom);

	public static String createRandomSeed() {
		return Long.toString(RNG.get().nextLong());
	}

	public static Random create(String seed) {
		try {
			Random random = newRandom(Long.parseLong(seed));
			current.set(random);
			return random;
		} catch (NumberFormatException nfe) {
			throw new JqwikException(String.format("[%s] is not a valid random seed.", seed));
		}
	}

	public static Random newRandom() {
		return new Random();
	}

	public static Random newRandom(final long seed) {
		return new Random(seed);
	}

	public static Random current() {
		return current.get();
	}

}
