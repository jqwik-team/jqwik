package net.jqwik.api;

import java.util.*;
import java.util.function.*;

public class Builder<T> {

	private final List<Function<T, T>> transformers = new ArrayList<>();

	public static <T> Builder<T> from(Arbitrary<T> arbitrary) {
		return new Builder<T>(arbitrary);
	}

	private final Arbitrary<T> arbitrary;

	public Builder(Arbitrary<T> arbitrary) {this.arbitrary = arbitrary;}

	public T build(JqwikRandom random) {
		RandomGenerator<T> generator = arbitrary.generator(1);
		T value = generator.next(random).value();
		return transform(value, transformers);
	}

	private T transform(T value, List<Function<T, T>> transformers) {
		if (transformers.isEmpty()) {
			return value;
		}
		T transformedValue = transformers.remove(0).apply(value);
		return transform(transformedValue, transformers);
	}

	public Builder<T> with(Function<T, T> transformer) {
		transformers.add(transformer);
		return this;
	}
}
