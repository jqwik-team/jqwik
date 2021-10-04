package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;

class Memoize {

	private static Store<Map<Tuple3<Arbitrary<?>, Integer, Boolean>, RandomGenerator<?>>> generatorStore() {
		return Store.getOrCreate(Memoize.class, Lifespan.PROPERTY, () -> new MemoizeLruCache<>(500));
	}

	@SuppressWarnings("unchecked")
	static <U> RandomGenerator<U> memoizedGenerator(
			Arbitrary<U> arbitrary,
			int genSize,
			boolean withEdgeCases,
			Supplier<RandomGenerator<U>> generatorSupplier
	) {
		Tuple3<Arbitrary<?>, Integer, Boolean> key = Tuple.of(arbitrary, genSize, withEdgeCases);

		RandomGenerator<?> generator = computeIfAbsent(
				generatorStore().get(),
				key,
				ignore -> generatorSupplier.get()
		);
		return (RandomGenerator<U>) generator;
	}

	// Had to roll my on computeIfAbsent because HashMap.computeIfAbsent()
	// does not allow modifications of the map within the mapping function
	private static <K, V> V computeIfAbsent(
			Map<K, V> cache,
			K key,
			Function<? super K, ? extends V> mappingFunction
	) {
		V result = cache.get(key);

		if (result == null) {
			result = mappingFunction.apply(key);
			cache.put(key, result);
		}

		return result;
	}

	private static class MemoizeLruCache<K, V> extends LinkedHashMap<K, V> {
		private final int maxSize;

		MemoizeLruCache(int maxSize) {
			super(maxSize + 1, 1, true);
			this.maxSize = maxSize;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return size() > maxSize;
		}
	}
}
