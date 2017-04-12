package net.jqwik.properties;

public class Arbitraries {
	public static <T> Arbitrary<T> fromGenerator(Generator<T> generator) {
		return new Arbitrary<T>() {
			@Override
			public Generator<T> generator(long seed) {
				return generator;
			}
		};
	}
}
