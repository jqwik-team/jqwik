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
	private List<Tuple.Tuple2<Integer, TransformerProvider<T>>> weightedProviders;
	private final Supplier<? extends T> initialSupplier;

	public DefaultChainArbitrary(Supplier<? extends T> initialSupplier) {
		this.initialSupplier = initialSupplier;
		this.weightedProviders = new ArrayList<>();
	}

	// TODO: Change users to use other constructor
	DefaultChainArbitrary(
		Supplier<? extends T> initialSupplier,
		List<Tuple.Tuple2<Integer, TransformerProvider<T>>> providerFrequencies
	) {
		this.initialSupplier = initialSupplier;
		this.weightedProviders = providerFrequencies;
	}

	@Override
	public RandomGenerator<Chain<T>> generator(int genSize) {
		final int effectiveMaxTransformations =
			this.maxTransformations != Integer.MIN_VALUE ? this.maxTransformations : (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		Function<Random, TransformerProvider<T>> providerGenerator = new ChooseRandomlyByFrequency<>(weightedProviders);
		return random -> new ShrinkableChain<>(
			random.nextLong(),
			initialSupplier,
			providerGenerator,
			changeDetectorSupplier,
			effectiveMaxTransformations,
			genSize
		);
	}

	@Override
	public ChainArbitrary<T> provideTransformer(int weight, TransformerProvider<T> provider) {
		DefaultChainArbitrary<T> clone = typedClone();
		List<Tuple.Tuple2<Integer, TransformerProvider<T>>> newWeightedTransformers = new ArrayList<>(weightedProviders);
		newWeightedTransformers.add(Tuple.of(weight, provider));
		clone.weightedProviders = newWeightedTransformers;
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
}
