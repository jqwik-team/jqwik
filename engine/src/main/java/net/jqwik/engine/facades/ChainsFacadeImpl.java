package net.jqwik.engine.facades;

import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.properties.state.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * Is loaded through reflection in api module
 */
public class ChainsFacadeImpl extends Chains.ChainsFacade {

	@Override
	public <T> ChainArbitrary<T> chains(
		Supplier<T> initialSupplier,
		List<Tuple2<Integer, TransformerProvider<T>>> providerFrequencies
	) {
		return new DefaultChainArbitrary<>(initialSupplier, providerFrequencies);
	}
}
