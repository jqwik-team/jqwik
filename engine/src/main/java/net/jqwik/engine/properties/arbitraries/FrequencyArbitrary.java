package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

import org.jspecify.annotations.*;

public class FrequencyArbitrary<T extends @Nullable Object> extends UseGeneratorsArbitrary<T> {
	private final List<Tuple.Tuple2<Integer, T>> frequencies;

	public FrequencyArbitrary(List<Tuple.Tuple2<Integer, T>> frequencies) {
		super(
			RandomGenerators.frequency(frequencies),
			max -> ExhaustiveGenerators.choose(valuesOf(frequencies), max),
			maxEdgeCases -> EdgeCasesSupport.choose(valuesOf(frequencies), maxEdgeCases)
		);
		this.frequencies = frequencies;
	}

	private static <T extends @Nullable Object> List<T> valuesOf(List<? extends Tuple.Tuple2<Integer, T>> frequencies) {
		return frequencies.stream().map(Tuple.Tuple2::get2).collect(Collectors.toList());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FrequencyArbitrary<?> that = (FrequencyArbitrary<?>) o;
		return frequencies.equals(that.frequencies);
	}

	@Override
	public int hashCode() {
		return frequencies.hashCode();
	}
}
