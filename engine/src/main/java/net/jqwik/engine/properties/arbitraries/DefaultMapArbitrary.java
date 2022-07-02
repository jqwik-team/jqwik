package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class DefaultMapArbitrary<K, V> extends ArbitraryDecorator<Map<K, V>> implements MapArbitrary<K, V> {

	private final Arbitrary<K> keysArbitrary;
	private final Arbitrary<V> valuesArbitrary;

	private int minSize = 0;
	private int maxSize = RandomGenerators.DEFAULT_COLLECTION_SIZE;
	private RandomDistribution sizeDistribution = null;

	private Set<FeatureExtractor<K>> keyUniquenessExtractors = new LinkedHashSet<>();
	private Set<FeatureExtractor<V>> valueUniquenessExtractors = new LinkedHashSet<>();

	public DefaultMapArbitrary(Arbitrary<K> keysArbitrary, Arbitrary<V> valuesArbitrary) {
		this.keysArbitrary = keysArbitrary;
		this.valuesArbitrary = valuesArbitrary;
	}

	@Override
	public MapArbitrary<K, V> ofMinSize(int minSize) {
		DefaultMapArbitrary<K, V> clone = typedClone();
		clone.minSize = minSize;
		return clone;
	}

	@Override
	public MapArbitrary<K, V> ofMaxSize(int maxSize) {
		DefaultMapArbitrary<K, V> clone = typedClone();
		clone.maxSize = maxSize;
		return clone;
	}

	@Override
	public MapArbitrary<K, V> withSizeDistribution(RandomDistribution distribution) {
		DefaultMapArbitrary<K, V> clone = typedClone();
		clone.sizeDistribution = distribution;
		return clone;
	}

	@Override
	protected Arbitrary<Map<K, V>> arbitrary() {
		// Using list of generated Map.Entry does not work because of potential duplicate keys
		SetArbitrary<K> keySetArbitrary = createKeySetArbitrary();
		Arbitrary<List<K>> keySets = keySetArbitrary.map(ArrayList::new);
		return keySets.flatMap(keys -> {
			int mapSize = keys.size();
			ListArbitrary<V> valueListArbitrary = createValueListArbitrary(mapSize);
			return valueListArbitrary.map(
					values -> {
						Map<K, V> map = new LinkedHashMap<>();
						for (int i = 0; i < mapSize; i++) {
							K key = keys.get(i);
							V value = values.get(i);
							map.put(key, value);
						}
						return map;
					});
		});
	}

	private ListArbitrary<V> createValueListArbitrary(int size) {
		ListArbitrary<V> valueListArbitrary = valuesArbitrary.list().ofSize(size);
		for (FeatureExtractor<V> extractor : valueUniquenessExtractors) {
			valueListArbitrary = valueListArbitrary.uniqueElements(extractor);
		}
		return valueListArbitrary;
	}

	private SetArbitrary<K> createKeySetArbitrary() {
		SetArbitrary<K> keySetArbitrary = keysArbitrary.set().ofMinSize(minSize).ofMaxSize(maxSize).withSizeDistribution(sizeDistribution);
		for (FeatureExtractor<K> extractor : keyUniquenessExtractors) {
			keySetArbitrary = keySetArbitrary.uniqueElements(extractor);
		}
		return keySetArbitrary;
	}

	@Override
	public MapArbitrary<K, V> uniqueKeys(Function<K, Object> by) {
		DefaultMapArbitrary<K, V> clone = typedClone();
		clone.keyUniquenessExtractors = new LinkedHashSet<>(keyUniquenessExtractors);
		clone.keyUniquenessExtractors.add(by::apply);
		return clone;
	}

	@Override
	public MapArbitrary<K, V> uniqueValues(Function<V, Object> by) {
		DefaultMapArbitrary<K, V> clone = typedClone();
		clone.valueUniquenessExtractors = new LinkedHashSet<>(valueUniquenessExtractors);
		clone.valueUniquenessExtractors.add(by::apply);
		return clone;
	}

	@Override
	public MapArbitrary<K, V> uniqueValues() {
		return uniqueValues(FeatureExtractor.identity());
	}
}
