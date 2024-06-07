package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.state.*;
import net.jqwik.engine.properties.state.*;

import org.jspecify.annotations.*;

/**
 * Is loaded through reflection in api module
 */
public class ActionChainFacadeImpl extends ActionChain.ActionChainFacade {
	@Override
	public <T extends @Nullable Object> ActionChainArbitrary<T> startWith(Supplier<? extends T> initialSupplier) {
		return new DefaultActionChainArbitrary<>(initialSupplier);
	}
}
