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
	public <T> Store<T> create(Object identifier, Lifespan lifespan, Supplier<T> initializer) {
		TestDescriptor scope = CurrentTestDescriptor.get();
		return StoreRepository.getCurrent().create(scope, identifier, lifespan, initializer);
	}

	@Override
	public <T> Store<T> get(Object identifier) {
		TestDescriptor retriever = CurrentTestDescriptor.get();
		Optional<? extends Store<T>> store = StoreRepository.getCurrent().get(retriever, identifier);
		return store.orElseThrow(() -> new CannotFindStoreException(identifier, retriever.getUniqueId().toString()));
	}

	@Override
	public <T> Store<T> free(Supplier<T> initializer) {
		return new Store<T>() {
			T t = initializer.get();

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
				t = initializer.get();
			}

			@Override
			public Store<T> onClose(Consumer<T> onCloseCallback) {
				return this;
			}

		};
	}
}
