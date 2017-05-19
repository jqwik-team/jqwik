package net.jqwik.newArbitraries;

public class NArbitraries {

	public static <T> NArbitrary<T> fromGenerator(NShrinkableGenerator<T> generator) {
		return tries -> generator;
	}

	@SafeVarargs
	public static <U> NArbitrary<U> of(U... values) {
		return fromGenerator(NShrinkableGenerators.choose(values));
	}

	public static <T extends Enum> NArbitrary<T> of(Class<T> enumClass) {
		return fromGenerator(NShrinkableGenerators.choose(enumClass));
	}

	public static NArbitrary<Integer> integer() {
		return new NIntegerArbitrary();
	}

	public static NArbitrary<Integer> integer(int min, int max) {
		return new NIntegerArbitrary(min, max);
	}

}
