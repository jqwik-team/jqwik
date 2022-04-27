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
	private final Function<Random, TransformerProvider<T>> providerGenerator;
	private final Supplier<? extends T> initialSupplier;

	public DefaultChainArbitrary(
		Supplier<? extends T> initialSupplier,
		List<Tuple.Tuple2<Integer, TransformerProvider<T>>> providerFrequencies
	) {
		this.initialSupplier = initialSupplier;
		this.providerGenerator = new ChooseRandomlyByFrequency<>(providerFrequencies);
	}

	@Override
	public RandomGenerator<Chain<T>> generator(int genSize) {
		final int effectiveMaxTransformations =
			this.maxTransformations != Integer.MIN_VALUE ? this.maxTransformations : (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		return random -> new ShrinkableChain<T>(
			random.nextLong(),
			initialSupplier,
			providerGenerator,
			changeDetectorSupplier,
			effectiveMaxTransformations,
			genSize
		);
	}

	@Override
	public ChainArbitrary<T> withMaxTransformations(int maxTransformations) {
		DefaultChainArbitrary<T> clone = typedClone();
		clone.maxTransformations = maxTransformations;
		return clone;
	}

	@Override
	public ChainArbitrary<T> detectChangesWith(Supplier<ChangeDetector<T>> changeDetectorSupplier) {
		DefaultChainArbitrary<T> clone = typedClone();
		clone.changeDetectorSupplier = changeDetectorSupplier;
		return clone;
	}

	@Override
	public EdgeCases<Chain<T>> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}
}
