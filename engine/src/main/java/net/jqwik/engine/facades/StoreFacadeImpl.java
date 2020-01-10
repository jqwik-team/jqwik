package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;

/**
 * Is loaded through reflection in api module
 */
public class StoreFacadeImpl extends Store.StoreFacade {

	private static StoreRepository repository = new StoreRepository();

	@Override
	public <T> Store<T> create(Store.Visibility visibility, String name, Supplier<T> initializer) {
		return repository.create(visibility, CurrentTestDescriptor.get(), name, initializer);
	}

	@Override
	public <T> Optional<Store<T>> get(String name, Class<T> type) {
		return repository.get(CurrentTestDescriptor.get(), name, type);
	}
}
