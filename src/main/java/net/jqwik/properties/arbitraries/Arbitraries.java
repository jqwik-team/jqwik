package net.jqwik.properties.arbitraries;

import net.jqwik.properties.*;

import java.util.*;

public class Arbitraries {

	public static <T> Arbitrary<T> fromGenerator(RandomGenerator<T> generator) {
		return new Arbitrary<T>() {
			@Override
			public RandomGenerator<T> generator(long seed, int tries) {
				return generator;
			}
		};
	}

	@SafeVarargs
	public static <U> Arbitrary<U> of(U... values) {
		return fromGenerator(RandomGenerators.choose(values));
	}

	public static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(RandomGenerators.choose(enumClass));
	}

	public static <T> Arbitrary<List<T>> list(Arbitrary<T> elementArbitrary, int maxSize) {
		return new Arbitrary<List<T>>() {
			@Override
			public RandomGenerator<List<T>> generator(long seed, int tries) {
				RandomGenerator<T> elementGenerator = elementArbitrary.generator(seed, tries);
				RandomGenerator<List<T>> generator = RandomGenerators.list(elementGenerator, maxSize);
				return generator;
			}
		};
	}
}