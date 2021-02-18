package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;

public class Memoize {

	private static final Store<Map<Tuple3<Arbitrary<?>, Integer, Boolean>, RandomGenerator<?>>> generatorStore = createStore();

	private static Store<Map<Tuple3<Arbitrary<?>, Integer, Boolean>, RandomGenerator<?>>> createStore() {
		return Store.create(Memoize.class, Lifespan.PROPERTY, HashMap::new);
	}

	@SuppressWarnings("unchecked")
	public static <U> RandomGenerator<U> memoizedGenerator(
			Arbitrary<U> arbitrary,
			int genSize,
			boolean withEmbeddedEdgeCases
	) {
		Tuple3<Arbitrary<?>, Integer, Boolean> key = Tuple.of(arbitrary, genSize, withEmbeddedEdgeCases);
		RandomGenerator<?> generator = generatorStore.get().computeIfAbsent(
				key,
				ignore -> arbitrary.generator(genSize, withEmbeddedEdgeCases));
		return (RandomGenerator<U>) generator;
	}

}
