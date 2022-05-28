package net.jqwik.docs.state.mystore;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class MyStore<K, V> {

	private final List<Tuple2<K, V>> tuples = new ArrayList<>();

	public Optional<V> get(K key) {
		return tuples.stream()
					 .filter(tuple -> tuple.get1().equals(key))
					 .map(Tuple2::get2)
					 .findFirst();
	}

	public void store(K key, V value) {
		tuples.add(0, Tuple.of(key, value));
	}

	public void remove(K key) {
		for (Tuple2<K, V> tuple : tuples) {
			if (tuple.get1().equals(key)) {
				tuples.remove(tuple);
				break;
			}
		}
	}

	public boolean isEmpty() {
		return tuples.isEmpty();
	}

	public Set<K> keys() {
		return tuples.stream().map(Tuple1::get1).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	public String toString() {
		Set<String> contents = keys().stream()
									 .map(k -> String.format("%s=%s", k, get(k).orElse(null)))
									 .collect(Collectors.toCollection(LinkedHashSet::new));
		return String.format("Store %s", contents);
	}
}
