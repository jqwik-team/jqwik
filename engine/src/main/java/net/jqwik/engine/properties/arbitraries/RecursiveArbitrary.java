package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;

public class RecursiveArbitrary<T> implements Arbitrary<T> {
	private final Supplier<? extends Arbitrary<T>> base;
	private final Function<? super Arbitrary<T>, ? extends Arbitrary<T>> recur;
	private final int depth;

	// Not used for exhaustive generation
	private final Arbitrary<T> arbitrary;

	private boolean isGeneratorMemoizable = true;

	public RecursiveArbitrary(Supplier<? extends Arbitrary<T>> base, Function<? super Arbitrary<T>, ? extends Arbitrary<T>> recur, int depth) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RecursiveArbitrary<?> that = (RecursiveArbitrary<?>) o;
		if (depth != that.depth) return false;
		if (!LambdaSupport.areEqual(base, that.base)) return false;
		return LambdaSupport.areEqual(recur, that.recur);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(base.getClass(), recur.getClass(), depth);
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
