package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.Store.*;

public class StoreRepository {

	private static StoreRepository current;

	// I hate this singleton as much as any singleton.
	// It seems to be necessary for the Store API though :-(
	public synchronized static StoreRepository getCurrent() {
		if (current == null) {
			current = new StoreRepository();
		}
		return current;
	}

	private Set<ScopedStore<?>> stores = new HashSet<>();

	public <T> ScopedStore<T> create(
		TestDescriptor scope,
		Object identifier,
		Visibility visibility,
		Supplier<T> initializer
	) {
		if (visibility == null) {
			throw new IllegalArgumentException("visibility must not be null");
		}
		if (initializer == null) {
			throw new IllegalArgumentException("initializer must not be null");
		}
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}
		ScopedStore<T> store = new ScopedStore<>(identifier, visibility, scope, initializer);
		addStore(identifier, store);
		return store;
	}

	private <T> void addStore(Object identifier, ScopedStore<T> newStore) {
		Optional<ScopedStore<?>> conflictingStore =
			stores.stream()
				  .filter(store -> store.getIdentifier().equals(newStore.getIdentifier()))
				  .filter(store -> store.isVisibleFor(newStore.getScope()))
				  .findFirst();

		conflictingStore.ifPresent(existingStore -> {
			String message = String.format(
				"You cannot create %s with identifier [%s]. It conflicts with existing %s",
				newStore,
				identifier.toString(),
				conflictingStore
			);
			throw new JqwikException(message);
		});

		stores.add(newStore);
	}

	public <T> Optional<ScopedStore<T>> get(TestDescriptor retriever, Object identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}

		//noinspection unchecked
		return stores.stream()
					 .filter(store -> store.getIdentifier().equals(identifier))
					 .filter(store -> store.isVisibleFor(retriever))
					 .map(store -> (ScopedStore<T>) store)
					 .findFirst();
	}

	public void removeStoresFor(TestDescriptor scope) {
		stores.removeIf(store -> store.getScope().equals(scope));
	}
}
