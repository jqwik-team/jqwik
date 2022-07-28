package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.support.*;

public class DefaultChainArbitrary<T> extends TypedCloneable implements ChainArbitrary<T> {

	private int maxTransformations = Integer.MIN_VALUE;
	private Supplier<ChangeDetector<T>> changeDetectorSupplier = ChangeDetector::alwaysTrue;
	private List<Tuple.Tuple2<Integer, Transformation<T>>> weightedTransformations = new ArrayList<>();
	private final Supplier<? extends T> initialSupplier;

	public DefaultChainArbitrary(Supplier<? extends T> initialSupplier) {
		this.initialSupplier = initialSupplier;
	}

	@Override
	public RandomGenerator<Chain<T>> generator(int genSize) {
		final int effectiveMaxTransformations =
			this.maxTransformations != Integer.MIN_VALUE ? this.maxTransformations : (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		Function<Random, Transformation<T>> transformationGenerator = new ChooseRandomlyByFrequency<>(weightedTransformations);
		return random -> new ShrinkableChain<>(
			random.nextLong(),
			initialSupplier,
			transformationGenerator,
			changeDetectorSupplier,
			effectiveMaxTransformations,
			genSize
		);
	}

	@Override
	public ChainArbitrary<T> addTransformation(int weight, Transformation<T> transformation) {
		if (weight <= 0) {
			throw new IllegalArgumentException("Weight must be at least 1");
		}
		DefaultChainArbitrary<T> clone = typedClone();
		List<Tuple.Tuple2<Integer, Transformation<T>>> newWeightedTransformations = new ArrayList<>(weightedTransformations);
		newWeightedTransformations.add(Tuple.of(weight, transformation));
		clone.weightedTransformations = newWeightedTransformations;
		return clone;
	}

	@Override
	public ChainArbitrary<T> withMaxTransformations(int maxTransformations) {
		DefaultChainArbitrary<T> clone = typedClone();
		clone.maxTransformations = maxTransformations;
		return clone;
	}

	@Override
	public ChainArbitrary<T> improveShrinkingWith(Supplier<ChangeDetector<T>> changeDetectorSupplier) {
		DefaultChainArbitrary<T> clone = typedClone();
		clone.changeDetectorSupplier = changeDetectorSupplier;
		return clone;
	}

	@Override
	public EdgeCases<Chain<T>> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}

	@Override
	public boolean isGeneratorMemoizable() {
		// Not memoizable, because any non-memoizable arbitrary could be used in transformer providers.
		return false;
	}

}
