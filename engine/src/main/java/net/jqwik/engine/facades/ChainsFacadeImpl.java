package net.jqwik.engine.facades;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.state.ChainArbitrary;
import net.jqwik.api.state.Chains;
import net.jqwik.engine.properties.state.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Is loaded through reflection in api module
 */
public class ChainsFacadeImpl extends Chains.ChainsFacade {

	@Override
	public <T> ChainArbitrary<T> chains(
		Supplier<T> initialSupplier,
		Function<Supplier<T>, Arbitrary<Chains.Mutator<T>>> chainGenerator
	) {
		return new DefaultChainArbitrary<>(initialSupplier, chainGenerator);
	}
}
