package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.properties.*;

public class Arbitraries {
	public static <T> Arbitrary<T> fromGenerator(Generator<T> generator) {
		return new Arbitrary<T>() {
			@Override
			public Generator<T> generator(long seed, int tries) {
				return generator;
			}
		};
	}

	public static <T> Arbitrary<T> fromGenerator(RandomGenerator<T> generator) {
		return new Arbitrary<T>() {
			@Override
			public Generator<T> generator(long seed, int tries) {
				final Random random = new Random(seed);
				return () -> generator.next(random);
			}
		};
	}
}
