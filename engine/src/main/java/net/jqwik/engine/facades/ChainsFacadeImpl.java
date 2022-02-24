package net.jqwik.engine.facades;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.state.*;
import net.jqwik.engine.properties.state.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Is loaded through reflection in api module
 */
public class ChainsFacadeImpl extends Chains.ChainsFacade {

	@Override
	public <T> ChainArbitrary<T> chains(
		Supplier<T> initialSupplier,
		List<Function<Supplier<T>, Arbitrary<Step<T>>>> chainGenerators
	) {
		return new DefaultChainArbitrary<>(initialSupplier, chainGenerators);
	}
}
