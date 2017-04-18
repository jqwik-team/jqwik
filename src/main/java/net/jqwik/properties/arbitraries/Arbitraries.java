package net.jqwik.properties.arbitraries;

import net.jqwik.properties.*;

public class Arbitraries {
	public static <T> Arbitrary<T> fromGenerator(RandomGenerator<T> generator) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(long seed, int tries) {
				return generator;
			}
		};
	}
}
