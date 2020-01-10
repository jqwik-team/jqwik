package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.Store.*;

public class StoreRepository {

	private Map<String, Set<StoreImpl<?>>> stores = new HashMap<>();

	public <T> Store<T> create(Visibility visibility, TestDescriptor owner, String name, Supplier<T> initializer) {
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
		StoreImpl<T> store = new StoreImpl<>(visibility, owner, initializer);
		Set<StoreImpl<?>> storesOfThisName = stores.getOrDefault(key, new HashSet<>());
		storesOfThisName.add(store);
		stores.put(key, storesOfThisName);
		return store;
	}

	public <T> Optional<Store<T>> get(TestDescriptor retriever, String name, Class<T> type) {
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		String key = name.trim();
		if (key.isEmpty()) {
			throw new IllegalArgumentException("name must not be empty");
		}

		Set<StoreImpl<?>> storesOfThisName = stores.getOrDefault(key, Collections.emptySet());
		//noinspection unchecked
		return storesOfThisName.stream()
							   .filter(store -> store.isVisibleFor(retriever))
							   .map(store -> (Store<T>) store)
							   .findFirst();
	}

}
