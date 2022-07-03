package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class RecursiveArbitrary<T> implements Arbitrary<T> {
	private final Supplier<Arbitrary<T>> base;
	private final Function<Arbitrary<T>, Arbitrary<T>> recur;
	private final int depth;

	// Not used for exhaustive generation
	private final Arbitrary<T> arbitrary;

	private boolean isGeneratorMemoizable = true;

	public RecursiveArbitrary(Supplier<Arbitrary<T>> base, Function<Arbitrary<T>, Arbitrary<T>> recur, int depth) {
		this.base = base;
		this.recur = recur;
		this.depth = depth;
		this.arbitrary = iteratedArbitrary();
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return arbitrary.generator(genSize);
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		// Very deep nesting tends to overflow the stack
		if (depth > 100) {
			return EdgeCases.none();
		}
		return arbitrary.edgeCases(maxEdgeCases);
	}

	@Override
	public boolean isGeneratorMemoizable() {
		return isGeneratorMemoizable;
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		// The straightforward implementation can easily overflow:
		// return arbitrary.exhaustive(maxNumberOfSamples);

		Arbitrary<T> current = base.get();
		Optional<ExhaustiveGenerator<T>> last = current.exhaustive(maxNumberOfSamples);
		for (int i = 0; i < depth; i++) {
			if (!last.isPresent()) {
				return Optional.empty();
			}
			current = recur.apply(current);
			last = current.exhaustive(maxNumberOfSamples);
		}
		return last;
	}

	private Arbitrary<T> iteratedArbitrary() {
		// Real recursion can blow the stack
		Arbitrary<T> current = base.get();
		isGeneratorMemoizable = current.isGeneratorMemoizable();
		for (int i = 0; i < depth; i++) {
			current = recur.apply(current);
			if (!current.isGeneratorMemoizable()) {
				isGeneratorMemoizable = false;
			}
		}
		return current;
	}

}
