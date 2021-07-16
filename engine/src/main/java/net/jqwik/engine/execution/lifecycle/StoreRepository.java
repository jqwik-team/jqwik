package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

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

	private static class IdentifiedStores extends HashMap<TestDescriptor, ScopedStore<?>> {}

	private final Map<Object, IdentifiedStores> stores = new HashMap<>();

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
		IdentifiedStores identifiedStores = stores.get(newStore.getIdentifier());
		if (identifiedStores == null) {
			identifiedStores = new IdentifiedStores();
		}

		Optional<ScopedStore<?>> conflictingStore =
			identifiedStores
				.values()
				.stream()
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

		identifiedStores.put(newStore.getScope(), newStore);
		stores.put(identifier, identifiedStores);
	}

	private <T> boolean isVisibleInAncestorOrDescendant(ScopedStore<T> newStore, ScopedStore<?> store) {
		return store.isVisibleFor(newStore.getScope()) || newStore.isVisibleFor(store.getScope());
	}

	public <T> Optional<ScopedStore<T>> get(TestDescriptor retriever, Object identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}

		IdentifiedStores identifiedStores = stores.get(identifier);
		if (identifiedStores == null) {
			return Optional.empty();
		} else {
			//noinspection unchecked
			return identifiedStores.values()
								   .stream()
								   .filter(store -> store.isVisibleFor(retriever))
								   .map(store -> (ScopedStore<T>) store)
								   .findFirst();
		}
	}

	public void finishScope(TestDescriptor scope) {
		List<ScopedStore<?>> storesToRemove =
			stores
				.values()
				.stream()
				.flatMap(identifiedStores -> identifiedStores.values().stream())
				.filter(store -> isStoreIn(store, scope))
				.collect(Collectors.toList());

		for (ScopedStore<?> store : storesToRemove) {
			store.close();
			stores.get(store.getIdentifier()).remove(store.getScope());
		}
	}

	private boolean isStoreIn(ScopedStore<?> store, TestDescriptor scope) {
		return store.getScope().equals(scope) || scope.getDescendants().contains(store.getScope());
	}

	public void finishProperty(TestDescriptor scope) {
		stores
			.values()
			.stream()
			.flatMap(identifiedStores -> identifiedStores.values().stream())
			.filter(store -> store.lifespan() == Lifespan.PROPERTY)
			.filter(store -> store.isVisibleFor(scope))
			.forEach(Store::reset);
	}

	public void finishTry(TestDescriptor scope) {
		stores
			.values()
			.stream()
			.flatMap(identifiedStores -> identifiedStores.values().stream())
			.filter(store -> store.lifespan() == Lifespan.TRY)
			.filter(store -> store.isVisibleFor(scope))
			.forEach(Store::reset);
	}
}
