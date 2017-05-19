package net.jqwik.newArbitraries;

public class NArbitraries {

	public static <T> NArbitrary<T> fromGenerator(NShrinkableGenerator<T> generator) {
		return tries -> generator;
	}


	@SafeVarargs
	public static <U> NArbitrary<U> of(U... values) {
		return fromGenerator(NShrinkableGenerators.choose(values));
	}

}
