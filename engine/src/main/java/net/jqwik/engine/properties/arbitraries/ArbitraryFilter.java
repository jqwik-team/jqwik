package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.*;

import org.jspecify.annotations.*;

public class ArbitraryFilter<T extends @Nullable Object> extends ArbitraryDelegator<T> {
	private final Predicate<? super T> filterPredicate;
	private final int maxMisses;

	public ArbitraryFilter(Arbitrary<T> self, Predicate<? super T> filterPredicate, int maxMisses) {
		super(self);
		this.filterPredicate = filterPredicate;
		this.maxMisses = maxMisses;
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return super.generator(genSize).filter(filterPredicate, maxMisses);
	}

	@Override
	public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
		return super.generatorWithEmbeddedEdgeCases(genSize).filter(filterPredicate, maxMisses);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return super.exhaustive(maxNumberOfSamples)
					.map(generator -> generator.filter(filterPredicate, maxMisses));
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.filter(super.edgeCases(maxEdgeCases), filterPredicate);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!super.equals(o)) return false;

		ArbitraryFilter<?> that = (ArbitraryFilter<?>) o;
		if (maxMisses != that.maxMisses) return false;
		return LambdaSupport.areEqual(filterPredicate, that.filterPredicate);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + maxMisses;
		return result;
	}
}
