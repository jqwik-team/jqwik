package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.Store.*;

public class StoreRepository {

	private Map<String, Set<ScopedStore<?>>> stores = new HashMap<>();

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
		String key = name.trim();
		if (key.isEmpty()) {
			throw new IllegalArgumentException("name must not be empty");
		}
		ScopedStore<T> store = new ScopedStore<>(visibility, scope, initializer);
		Set<ScopedStore<?>> storesOfThisName = stores.getOrDefault(key, new HashSet<>());
		storesOfThisName.add(store);
		stores.put(key, storesOfThisName);
		return store;
	}

	public <T> Optional<ScopedStore<T>> get(TestDescriptor retriever, String name, Class<T> type) {
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		String key = name.trim();
		if (key.isEmpty()) {
			throw new IllegalArgumentException("name must not be empty");
		}

		Set<ScopedStore<?>> storesOfThisName = stores.getOrDefault(key, Collections.emptySet());
		//noinspection unchecked
		return storesOfThisName.stream()
							   .filter(store -> store.isVisibleFor(retriever))
							   .map(store -> (ScopedStore<T>) store)
							   .findFirst();
	}

}
