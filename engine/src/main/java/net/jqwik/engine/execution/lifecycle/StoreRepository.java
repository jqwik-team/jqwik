package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.concurrent.*;
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

	private static class IdentifiedStores extends LinkedHashMap<TestDescriptor, ScopedStore<?>> {}

	private final Map<Object, IdentifiedStores> storesByIdentifier = new ConcurrentHashMap<>();

	public <T> ScopedStore<T> create(
		TestDescriptor scope,
		Object identifier,
		Lifespan lifespan,
		Supplier<T> initialValueSupplier
	) {
		if (scope == null) {
			throw new IllegalArgumentException("scope must not be null");
		}
		if (initialValueSupplier == null) {
			throw new IllegalArgumentException("initialValueSupplier code must not be null");
		}
		if (lifespan == null) {
			throw new IllegalArgumentException("lifespan must not be null");
		}
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}
		ScopedStore<T> store = new ScopedStore<>(identifier, lifespan, scope, initialValueSupplier);
		addStore(identifier, store);
		return store;
	}

	private <T> void addStore(Object identifier, ScopedStore<T> newStore) {
		IdentifiedStores identifiedStores = storesByIdentifier.computeIfAbsent(newStore.getIdentifier(), ignore -> new IdentifiedStores());

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
		storesByIdentifier.put(identifier, identifiedStores);
	}

	private <T> boolean isVisibleInAncestorOrDescendant(ScopedStore<T> newStore, ScopedStore<?> store) {
		return store.isVisibleFor(newStore.getScope()) || newStore.isVisibleFor(store.getScope());
	}

	public <T> Optional<ScopedStore<T>> get(TestDescriptor retriever, Object identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("identifier must not be null");
		}

		IdentifiedStores identifiedStores = storesByIdentifier.get(identifier);
		if (identifiedStores == null) {
			return Optional.empty();
		} else {
			return getFirstVisibleStore(retriever, identifiedStores);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Optional<ScopedStore<T>> getFirstVisibleStore(TestDescriptor retriever, IdentifiedStores identifiedStores) {
		return identifiedStores.values()
							   .stream()
							   .filter(store -> store.isVisibleFor(retriever))
							   .map(store -> (ScopedStore<T>) store)
							   .findFirst();
	}

	public void finishScope(TestDescriptor scope) {
		List<ScopedStore<?>> storesToRemove =
			streamAllStores()
				.filter(store -> isStoreIn(store, scope))
				.collect(Collectors.toList());

		// forEach does not work because map underlying the stream is changed
		for (ScopedStore<?> store : storesToRemove) {
			store.close();
			removeStore(store);
		}
	}

	private void removeStore(ScopedStore<?> store) {
		IdentifiedStores identifiedStores = storesByIdentifier.get(store.getIdentifier());
		identifiedStores.remove(store.getScope());
		if (identifiedStores.isEmpty()) {
			storesByIdentifier.remove(store.getIdentifier());
		}
	}

	private Stream<ScopedStore<?>> streamAllStores() {
		Collection<IdentifiedStores> values = new ArrayList<>(storesByIdentifier.values());
		return values
			.stream()
			.flatMap(identifiedStores -> new ArrayList<>(identifiedStores.values()).stream());

		// TODO: Above implementation tries to get rid of ConcurrentModificationException
		//       reported in https://github.com/jqwik-team/jqwik/issues/210.
		//       Evaluate if it really does.
		// return storesByIdentifier
		// 		   .values()
		// 		   .stream()
		// 		   .flatMap(identifiedStores -> identifiedStores.values().stream());
	}

	private boolean isStoreIn(ScopedStore<?> store, TestDescriptor scope) {
		return store.getScope().equals(scope) || scope.getDescendants().contains(store.getScope());
	}

	public void finishProperty(TestDescriptor scope) {
		streamAllStores()
			.filter(store -> store.lifespan() == Lifespan.PROPERTY)
			.filter(store -> store.isVisibleFor(scope))
			.forEach(Store::reset);
	}

	public void finishTry(TestDescriptor scope) {
		streamAllStores()
			.filter(store -> store.lifespan() == Lifespan.TRY)
			.filter(store -> store.isVisibleFor(scope))
			.forEach(Store::reset);
	}

	public int size() {
		return storesByIdentifier.values().stream().mapToInt(HashMap::size).sum();
	}
}
