package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class MapArbitrary<K, V> extends AbstractArbitraryBase implements SizableArbitrary<Map<K, V>> {

	private final Arbitrary<K> keysArbitrary;
	private final Arbitrary<V> valuesArbitrary;

	private int minSize = 0;
	private int maxSize = RandomGenerators.DEFAULT_COLLECTION_SIZE;

	public MapArbitrary(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		this.keysArbitrary = keysArbitrary;
		this.valuesArbitrary = valuesArbitrary;
	}

	@Override
	public SizableArbitrary<Map<K, V>> ofMinSize(int minSize) {
		MapArbitrary<K, V> clone = typedClone();
		clone.minSize = minSize;
		return clone;
	}

	@Override
	public SizableArbitrary<Map<K, V>> ofMaxSize(int maxSize) {
		MapArbitrary<K, V> clone = typedClone();
		clone.maxSize = maxSize;
		return clone;
	}

	@Override
	public RandomGenerator<Map<K, V>> generator(int genSize) {
		return mapArbitrary().generator(genSize);
	}

	private Arbitrary<Map<K, V>> mapArbitrary() {
		return entrySetArbitrary().map(entries -> {
			HashMap<K, V> map = new HashMap<>();
			for (Map.Entry<K, V> entry : entries) {
				map.put(entry.getKey(), entry.getValue());
			}
			return map;
		});
	}

	private Arbitrary<Set<Map.Entry<K, V>>> entrySetArbitrary() {
		return entryArbitrary().set().ofMinSize(minSize).ofMaxSize(maxSize);
	}

	private Arbitrary<Map.Entry<K, V>> entryArbitrary() {
		return Combinators.combine(keysArbitrary, valuesArbitrary).as(KeyDefinesIdentityEntry::new);
	}

	@Override
	public Optional<ExhaustiveGenerator<Map<K, V>>> exhaustive() {
		return mapArbitrary().exhaustive();
	}

	private static class KeyDefinesIdentityEntry<K, V> implements Map.Entry<K, V> {
		private final K key;
		private final V value;

		private KeyDefinesIdentityEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			KeyDefinesIdentityEntry<?, ?> that = (KeyDefinesIdentityEntry<?, ?>) o;

			return key.equals(that.key);
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}
	}
}
