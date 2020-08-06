package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class FrequencyOfGenerator<T> implements RandomGenerator<T> {

	private final List<Tuple2<Integer, Arbitrary<T>>> frequencies;
	private final int genSize;

	public FrequencyOfGenerator(List<Tuple2<Integer, Arbitrary<T>>> frequencies, int genSize) {
		this.frequencies = frequencies;
		this.genSize = genSize;
	}

	@Override
	public Shrinkable<T> next(Random random) {
		Shrinkable<T> next = RandomGenerators.frequency(frequencies).flatMap(a -> {
			return a;
		}, genSize).next(random);
		return next;
	}
}
