package net.jqwik.docs.state.mystore;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

import static org.assertj.core.api.Assertions.*;

@PropertyDefaults(tries = 100)
public class MyStoreExamples {

	/**
	 * This property should detect a bug that will occur when a key is updated and then deleted afterwards
	 */
	@Property(shrinking = ShrinkingMode.FULL, afterFailure = AfterFailureMode.RANDOM_SEED)
	void storeWorksAsExpected(@ForAll("storeActions") ActionChain<MyStore<Integer, String>> storeChain) {
		storeChain.run();
	}

	@Provide
	ActionChainArbitrary<MyStore<Integer, String>> storeActions() {
		return ActionChain.<MyStore<Integer, String>>startWith(MyStore::new)
						  .addAction(3, new StoreAnyValue())
						  .addAction(1, new UpdateValue())
						  .addAction(1, new RemoveValue())
						  .improveShrinkingWith(StoreChangesDetector::new);
	}

	static class StoreAnyValue implements Action.Independent<MyStore<Integer, String>> {
		@Override
		public Arbitrary<Transformer<MyStore<Integer, String>>> transformer() {
			return Combinators.combine(keys(), values())
							  .as((key, value) -> Transformer.mutate(
								  String.format("store %s=%s", key, value),
								  store -> {
									  store.store(key, value);
									  assertThat(store.isEmpty()).isFalse();
									  assertThat(store.get(key)).isEqualTo(Optional.of(value));
								  }
							  ));
		}
	}

	static class UpdateValue implements Action.Dependent<MyStore<Integer, String>> {
		@Override
		public boolean precondition(MyStore<Integer, String> store) {
			return !store.isEmpty();
		}

		@Override
		public Arbitrary<Transformer<MyStore<Integer, String>>> transformer(MyStore<Integer, String> state) {
			Arbitrary<Integer> existingKeys = Arbitraries.of(state.keys());
			return Combinators.combine(existingKeys, values())
							  .as((key, value) -> Transformer.mutate(
								  String.format("update %s=%s", key, value),
								  store -> {
									  store.store(key, value);
									  assertThat(store.isEmpty()).isFalse();
									  assertThat(store.get(key)).isEqualTo(Optional.of(value));
								  }
							  ));
		}
	}

	static class RemoveValue implements Action.Dependent<MyStore<Integer, String>> {
		@Override
		public boolean precondition(MyStore<Integer, String> store) {
			return !store.isEmpty();
		}

		@Override
		public Arbitrary<Transformer<MyStore<Integer, String>>> transformer(MyStore<Integer, String> state) {
			Arbitrary<Integer> existingKeys = Arbitraries.of(state.keys());
			return existingKeys.map(key -> Transformer.mutate(
				String.format("remove %s", key),
				store -> {
					store.remove(key);
					assertThat(store.get(key)).isNotPresent();
				}
			));
		}
	}

	private static Arbitrary<Integer> keys() {
		return Arbitraries.integers().between(1, Integer.MAX_VALUE);
	}

	private static Arbitrary<String> values() {
		return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(10);
	}

	private static class StoreChangesDetector<K, V> implements ChangeDetector<MyStore<K, V>> {

		private Set<Tuple.Tuple2<K, V>> entries;

		@Override
		public void before(MyStore<K, V> before) {
			this.entries = entries(before);
		}

		private Set<Tuple.Tuple2<K, V>> entries(MyStore<K, V> before) {
			return before.keys().stream()
						 .map(key -> Tuple.of(key, before.get(key).orElse(null)))
						 .collect(Collectors.toCollection(LinkedHashSet::new));
		}

		@Override
		public boolean hasChanged(MyStore<K, V> after) {
			return this.entries.equals(entries(after));
		}
	}
}
