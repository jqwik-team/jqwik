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

	private Set<FeatureExtractor<K>> keyUniquenessExtractors = new HashSet<>();
	private Set<FeatureExtractor<V>> valueUniquenessExtractors = new HashSet<>();

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
	protected Arbitrary<Map<K, V>> arbitrary() {
		// Using list of generated Map.Entry does not work because of potential duplicate keys
		SetArbitrary<K> keySetArbitrary = createKeySetArbitrary();
		Arbitrary<List<K>> keySets = keySetArbitrary.map(ArrayList::new);
		return keySets.flatMap(keys -> {
			int mapSize = keys.size();
			ListArbitrary<V> valueListArbitrary = createValueListArbitrary(mapSize);
			return valueListArbitrary.map(
					values -> {
						HashMap<K, V> map = new HashMap<>();
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
			valueListArbitrary = valueListArbitrary.uniqueness(extractor);
		}
		return valueListArbitrary;
	}

	private SetArbitrary<K> createKeySetArbitrary() {
		SetArbitrary<K> keySetArbitrary = keysArbitrary.set().ofMinSize(minSize).ofMaxSize(maxSize);
		for (FeatureExtractor<K> extractor : keyUniquenessExtractors) {
			keySetArbitrary = keySetArbitrary.uniqueness(extractor);
		}
		return keySetArbitrary;
	}

	@Override
	public EdgeCases<Map<K, V>> edgeCases() {
		EdgeCases<Map<K, V>> emptyMapEdgeCase =
				minSize == 0
						? EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(new HashMap<>()))
						: EdgeCases.none();
		EdgeCases<Map<K, V>> singleEntryEdgeCases =
				minSize <= 1
						? singleEntryEdgeCases()
						: EdgeCases.none();
		return EdgeCasesSupport.concat(emptyMapEdgeCase, singleEntryEdgeCases);
	}

	@Override
	public MapArbitrary<K, V> keyUniqueness(Function<K, Object> by) {
		DefaultMapArbitrary<K, V> clone = typedClone();
		clone.keyUniquenessExtractors = new HashSet<>(keyUniquenessExtractors);
		clone.keyUniquenessExtractors.add(by::apply);
		return clone;
	}

	@Override
	public MapArbitrary<K, V> valueUniqueness(Function<V, Object> by) {
		DefaultMapArbitrary<K, V> clone = typedClone();
		clone.valueUniquenessExtractors = new HashSet<>(valueUniquenessExtractors);
		clone.valueUniquenessExtractors.add(by::apply);
		return clone;
	}

	@Override
	public MapArbitrary<K, V> uniqueValues() {
		return valueUniqueness(FeatureExtractor.identity());
	}

	private EdgeCases<Map<K, V>> singleEntryEdgeCases() {
		return EdgeCasesSupport.flatMapArbitrary(
				keysArbitrary.edgeCases(),
				key -> valuesArbitrary.map(value -> {
					HashMap<K, V> map = new HashMap<>();
					map.put(key, value);
					return map;
				})
		);
	}
}
