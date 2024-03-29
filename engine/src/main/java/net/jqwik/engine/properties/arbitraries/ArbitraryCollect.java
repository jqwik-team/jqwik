package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;

public class ArbitraryCollect<T> implements Arbitrary<List<T>> {

	private final Arbitrary<T> elementArbitrary;
	private final Predicate<List<T>> until;

	public ArbitraryCollect(Arbitrary<T> elementArbitrary, Predicate<List<T>> until) {
		this.elementArbitrary = elementArbitrary;
		this.until = until;
	}

	@Override
	public RandomGenerator<List<T>> generator(final int genSize) {
		return elementArbitrary.generator(genSize).collect(until);
	}

	@Override
	public EdgeCases<List<T>> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArbitraryCollect<?> that = (ArbitraryCollect<?>) o;
		if (!elementArbitrary.equals(that.elementArbitrary)) return false;
		return LambdaSupport.areEqual(until, that.until);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(elementArbitrary, until.getClass());
	}
}
