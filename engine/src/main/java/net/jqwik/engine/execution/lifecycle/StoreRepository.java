package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.Store.*;

/**
 * StoreRepository and ScopedStore CANNOT handle concurrent execution of properties!
 */
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
		Lifespan lifespan,
		Supplier<T> initializer
	) {
		if (scope == null) {
			throw new IllegalArgumentException("scope must not be null");
		}
		if (initializer == null) {
			throw new IllegalArgumentException("initializer must not be null");
		}
		if (lifespan == null) {
			throw new IllegalArgumentException("lifespan must not be null");
		}
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}
		ScopedStore<T> store = new ScopedStore<>(identifier, lifespan, scope, initializer);
		addStore(identifier, store);
		return store;
	}

	private <T> void addStore(Object identifier, ScopedStore<T> newStore) {
		Optional<ScopedStore<?>> conflictingStore =
			stores.stream()
				  .filter(store -> store.getIdentifier().equals(newStore.getIdentifier()))
				  .filter(store -> isVisibleInAncestorOrDescendant(newStore, store))
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

	private <T> boolean isVisibleInAncestorOrDescendant(ScopedStore<T> newStore, ScopedStore<?> store) {
		return store.isVisibleFor(newStore.getScope()) || newStore.isVisibleFor(store.getScope());
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

	public void finishScope(TestDescriptor scope) {
		List<ScopedStore<?>> storesToRemove =
			stores
				.stream()
				.filter(store -> isStoreIn(store, scope))
				.collect(Collectors.toList());

		for (ScopedStore<?> store : storesToRemove) {
			store.close();
			stores.remove(store);
		}
	}

	private boolean isStoreIn(ScopedStore<?> store, TestDescriptor scope) {
		return store.getScope().equals(scope) || scope.getDescendants().contains(store.getScope());
	}

	public void finishProperty(TestDescriptor scope) {
		stores
			.stream()
			.filter(store -> store.lifespan() == Lifespan.PROPERTY)
			.filter(store -> store.isVisibleFor(scope))
			.forEach(ScopedStore::reset);
	}

	public void finishTry(TestDescriptor scope) {
		stores
			.stream()
			.filter(store -> store.lifespan() == Lifespan.TRY)
			.filter(store -> store.isVisibleFor(scope))
			.forEach(ScopedStore::reset);
	}
}
