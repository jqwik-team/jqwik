package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;

//Currently only used in DefaultTypeArbitrary
//Support configurators if this class is used somewhere else
class IgnoreGenerationErrorArbitrary<T> implements Arbitrary<T> {
	private Arbitrary<T> arbitrary;

	IgnoreGenerationErrorArbitrary(Arbitrary<T> arbitrary) {
		this.arbitrary = arbitrary;
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		RandomGenerator<T> generator = arbitrary.generator(genSize);

		return random -> {
			int count = 0;
			while (count++ < 1000) {
				try {
					Shrinkable<T> next = generator.next(random);
					return next;
				} catch (GenerationError ignore) {
				}
			}
			String message = String.format("Too many exceptions while generating values with %s", arbitrary.toString());
			throw new JqwikException(message);
		};
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		// Support exhaustive generation if this class is used somewhere else
		return Optional.empty();
	}

}
