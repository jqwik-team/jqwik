package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.Tuple.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.properties.state.*;

/**
 * Is loaded through reflection in api module
 */
public class ActionChainFacadeImpl extends ActionChain.ActionChainFacade {

	@Override
	public <T> ActionChainArbitrary<T> actionChains(
		Supplier<? extends T> initialSupplier,
		List<Tuple2<Integer, Action<T>>> actionFrequencies
	) {
		DefaultActionChainArbitrary<T> actionChainArbitrary = new DefaultActionChainArbitrary<>(initialSupplier);
		for (Tuple2<Integer, Action<T>> actionFrequency : actionFrequencies) {
			actionChainArbitrary = (DefaultActionChainArbitrary<T>) actionChainArbitrary.addAction(actionFrequency.get1(), actionFrequency.get2());
		}
		return actionChainArbitrary;
	}

	@Override
	public <T> ActionChainArbitrary<T> startWith(Supplier<? extends T> initialSupplier) {
		return new DefaultActionChainArbitrary<>(initialSupplier);
	}
}
