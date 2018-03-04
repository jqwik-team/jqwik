package net.jqwik.properties;

import net.jqwik.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class SourceOfRandomness {

	private static Supplier<Random> RNG = ThreadLocalRandom::current;

	private static ThreadLocal<Random> current = ThreadLocal.withInitial(Random::new);

	public static String createRandomSeed() {
		return Long.toString(RNG.get().nextLong());
	}

	public static Random create(String seed) {
		try {
			Random random = new Random(Long.parseLong(seed));
			current.set(random);
			return random;
		} catch (NumberFormatException nfe) {
			throw new JqwikException(String.format("[%s] is not a valid random seed.", seed));
		}
	}

	public static Random current() {
		return current.get();
	}

}
