package net.jqwik.engine.properties.state;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.properties.arbitraries.*;

public class DefaultChainArbitrary<T> extends TypedCloneable implements ChainArbitrary<T> {

	private int size = 0;
	private Supplier<T> initialSupplier;
	private Function<Supplier<T>, Arbitrary<T>> chainGenerator;

	public DefaultChainArbitrary(Supplier<T> initialSupplier, Function<Supplier<T>, Arbitrary<T>> chainGenerator) {
		this.initialSupplier = initialSupplier;
		this.chainGenerator = chainGenerator;
	}

	@Override
	public RandomGenerator<Chain<T>> generator(int genSize) {
		final int effectiveSize =
			size != 0 ? size : (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		return random -> new ShrinkableChain(random.nextLong(), initialSupplier, chainGenerator, effectiveSize);
	}

	@Override
	public ChainArbitrary<T> ofSize(int size) {
		DefaultChainArbitrary<T> clone = typedClone();
		clone.size = size;
		return clone;
	}

	@Override
	public EdgeCases<Chain<T>> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}
}
