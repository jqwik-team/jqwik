package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class ChooseValueArbitrary<T> extends FromGeneratorsArbitrary<T> {

	private final int hashCode;
	private final List<T> values;

	public ChooseValueArbitrary(List<T> values) {
		super(
			RandomGenerators.choose(values),
			max -> ExhaustiveGenerators.choose(values, max),
			maxEdgeCases -> EdgeCasesSupport.choose(values, maxEdgeCases)
		);
		hashCode = values.hashCode();
		this.values = values;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ChooseValueArbitrary<?> that = (ChooseValueArbitrary<?>) o;
		return values.equals(that.values);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
