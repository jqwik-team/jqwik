package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.Store.*;

public class StoreRepository {

	private static StoreRepository current;

	// I hate this singleton.
	// It seems to be necessary for the Store API though :-(
	public synchronized static StoreRepository getCurrent() {
		if (current == null) {
			current = new StoreRepository();
		}
		return current;
	}

	private Set<ScopedStore<?>> stores = new HashSet<>();

	public <T> ScopedStore<T> create(Visibility visibility, TestDescriptor scope, String name, Supplier<T> initializer) {
		if (visibility == null) {
			throw new IllegalArgumentException("visibility must not be null");
		}
		if (initializer == null) {
			throw new IllegalArgumentException("initializer must not be null");
		}
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		String normalizedName = normalize(name);
		if (normalizedName.isEmpty()) {
			throw new IllegalArgumentException("name must not be empty");
		}
		ScopedStore<T> store = new ScopedStore<>(normalizedName, visibility, scope, initializer);
		addStore(normalizedName, store);
		return store;
	}

	private <T> void addStore(String key, ScopedStore<T> newStore) {
		Optional<ScopedStore<?>> conflictingStore =
			stores.stream()
				  .filter(store -> store.getName().equals(newStore.getName()))
				  .filter(store -> store.isVisibleFor(newStore.getScope()))
				  .findFirst();

		conflictingStore.ifPresent(existingStore -> {
			String message = String.format(
				"You cannot create %s with name [%s]. It conflicts with existing %s",
				newStore,
				key,
				conflictingStore
			);
			throw new JqwikException(message);
		});

		stores.add(newStore);
	}

	public <T> Optional<ScopedStore<T>> get(TestDescriptor retriever, String name) {
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		String normalizedName = normalize(name);
		if (normalizedName.isEmpty()) {
			throw new IllegalArgumentException("name must not be empty");
		}

		//noinspection unchecked
		return stores.stream()
					 .filter(store -> store.getName().equals(normalizedName))
					 .filter(store -> store.isVisibleFor(retriever))
					 .map(store -> (ScopedStore<T>) store)
					 .findFirst();
	}

	private String normalize(String name) {
		return name.trim();
	}

	public void removeStoresFor(TestDescriptor scope) {
		stores.removeIf(store -> store.getScope().equals(scope));
	}
}
