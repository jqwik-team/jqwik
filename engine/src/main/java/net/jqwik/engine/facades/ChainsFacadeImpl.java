package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.properties.state.*;

/**
 * Is loaded through reflection in api module
 */
public class ChainsFacadeImpl extends Chains.ChainsFacade {

	@Override
	public <T> ChainArbitrary<T> chains(
		Supplier<? extends T> initialSupplier,
		List<Tuple2<Integer, TransformerProvider<T>>> providerFrequencies
	) {
		return new DefaultChainArbitrary<>(initialSupplier, providerFrequencies);
	}

	@Override
	public <T> ActionChainArbitrary<T> actionChains(
		Supplier<? extends T> initialSupplier,
		List<Tuple2<Integer, Action<T>>> actionFrequencies
	) {
		return new DefaultActionChainArbitrary<>(initialSupplier, actionFrequencies);
	}
}
