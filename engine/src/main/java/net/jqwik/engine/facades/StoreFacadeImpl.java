package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.Store.*;
import net.jqwik.engine.execution.lifecycle.*;

/**
 * Is loaded through reflection in api module
 */
public class StoreFacadeImpl extends Store.StoreFacade {

	@Override
	public <T> Store<T> create(Object identifier, Visibility visibility, Supplier<T> initializer) {
		// TODO: For global visibility choose root descriptor
		TestDescriptor scope = CurrentTestDescriptor.get();
		return StoreRepository.getCurrent().create(scope, identifier, initializer);
	}

	@Override
	public <T> Store<T> get(Object identifier) {
		TestDescriptor retriever = CurrentTestDescriptor.get();
		Optional<? extends Store<T>> store = StoreRepository.getCurrent().get(retriever, identifier);
		return store.orElseThrow(() -> new CannotFindStoreException(identifier, retriever));
	}
}
