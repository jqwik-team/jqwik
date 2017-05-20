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

	public static NArbitrary<Long> longInteger(long min, long max) {
		return new NLongArbitrary();
	}

	public static NArbitrary<Long> longInteger() {
		return new NLongArbitrary();
	}

	public static NArbitrary<String> string() {
		return new NStringArbitrary();
	}

	public static NArbitrary<String> string(char[] validChars, int maxSize) {
		return new NStringArbitrary(validChars, maxSize);
	}

	public static NArbitrary<String> string(char[] validChars) {
		return new NStringArbitrary(validChars);
	}

	public static NArbitrary<String> string(char from, char to, int maxSize) {
		return new NStringArbitrary(from, to, maxSize);
	}

	public static NArbitrary<String> string(char from, char to) {
		return new NStringArbitrary(from, to);
	}

	@SafeVarargs
	public static <T> NArbitrary<T> samples(T... samples) {
		return fromGenerator(NShrinkableGenerators.samples(samples));
	}

}
