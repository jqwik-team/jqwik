package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;

/**
 * Is loaded through reflection in api module
 */
public class StoreFacadeImpl extends Store.StoreFacade {

	@Override
	public <T> Store<T> create(Store.Visibility visibility, String name, Supplier<T> initializer) {
		return StoreRepository.getCurrent().create(visibility, CurrentTestDescriptor.get(), name, initializer);
	}

	@Override
	public <T> Store<T> get(String name) {
		TestDescriptor retriever = CurrentTestDescriptor.get();
		Optional<? extends Store<T>> store = StoreRepository.getCurrent().get(retriever, name);
		return store.orElseThrow(() -> new CannotFindStoreException(name, retriever));
	}
}
