package net.jqwik.engine.properties;

import java.util.*;
import java.util.concurrent.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class Memoize {

	private static final Map<Tuple3<Arbitrary<?>, Integer, Boolean>, RandomGenerator<?>> generators = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public static <U> RandomGenerator<U> memoizedGenerator(
			Arbitrary<U> arbitrary,
			int genSize,
			boolean withEmbeddedEdgeCases
	) {
		Tuple3<Arbitrary<?>, Integer, Boolean> key = Tuple.of(arbitrary, genSize, withEmbeddedEdgeCases);
		RandomGenerator<?> generator = generators.computeIfAbsent(
				key,
				ignore -> arbitrary.generator(genSize, withEmbeddedEdgeCases));
		return (RandomGenerator<U>) generator;
	}

}
