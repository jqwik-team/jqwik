package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import org.jspecify.annotations.*;
import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;

/**
 * Is loaded through reflection in api module
 */
public class StoreFacadeImpl extends Store.StoreFacade {

	@Override
	public <T extends @Nullable Object> Store<T> create(Object identifier, Lifespan lifespan, Supplier<? extends T> initialValueSupplier) {
		TestDescriptor scope = CurrentTestDescriptor.get();
		return StoreRepository.getCurrent().create(scope, identifier, lifespan, initialValueSupplier);
	}

	@Override
	public <T extends @Nullable Object> Store<T> get(Object identifier) {
		TestDescriptor retriever = CurrentTestDescriptor.get();
		Optional<? extends Store<T>> store = StoreRepository.getCurrent().get(retriever, identifier);
		return store.orElseThrow(() -> new CannotFindStoreException(identifier, retriever.getUniqueId().toString()));
	}

	@Override
	public <T extends @Nullable Object> Store<T> free(Supplier<? extends T> initialValueSupplier) {
		return new Store<T>() {
			T t = initialValueSupplier.get();

			@Override
			public T get() {
				return t;
			}

			@Override
			public Lifespan lifespan() {
				return Lifespan.RUN;
			}

			@Override
			public void update(Function<T, T> updater) {
				t = updater.apply(t);
			}

			@Override
			public void reset() {
				t = initialValueSupplier.get();
			}
		};
	}
}
