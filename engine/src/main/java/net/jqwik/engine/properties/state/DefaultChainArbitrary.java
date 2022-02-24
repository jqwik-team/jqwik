package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.properties.arbitraries.*;

public class DefaultChainArbitrary<T> extends TypedCloneable implements ChainArbitrary<T> {

	private int size = 0;
	private final Supplier<T> initialSupplier;
	private final List<Function<Supplier<T>, Arbitrary<Mutator<T>>>> chainGenerators;

	public DefaultChainArbitrary(
		Supplier<T> initialSupplier,
		List<Function<Supplier<T>, Arbitrary<Mutator<T>>>> chainGenerators
	) {
		this.initialSupplier = initialSupplier;
		this.chainGenerators = chainGenerators;
	}

	@Override
	public RandomGenerator<Chain<T>> generator(int genSize) {
		final int effectiveSize =
			size != 0 ? size : (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		return random -> new ShrinkableChain<T>(random.nextLong(), initialSupplier, chainGenerators, effectiveSize);
	}

	@Override
	public ChainArbitrary<T> ofMaxSize(int size) {
		DefaultChainArbitrary<T> clone = typedClone();
		clone.size = size;
		return clone;
	}

	@Override
	public EdgeCases<Chain<T>> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}
}
