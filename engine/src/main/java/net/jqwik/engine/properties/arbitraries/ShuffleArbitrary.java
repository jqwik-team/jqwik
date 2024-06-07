package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

import org.jspecify.annotations.*;

public class ShuffleArbitrary<T extends @Nullable Object> extends UseGeneratorsArbitrary<List<T>> {
	private final List<T> values;

	public ShuffleArbitrary(List<T> values) {
		super(
			RandomGenerators.shuffle(values),
			max -> ExhaustiveGenerators.shuffle(values, max),
			maxEdgeCases -> EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(values))
		);
		this.values = values;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ShuffleArbitrary<?> that = (ShuffleArbitrary<?>) o;
		return values.equals(that.values);
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}
}
