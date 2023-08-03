package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.support.*;

import java.util.function.*;

public class SupplyGeneratorArbitrary<T> implements Arbitrary<T> {

	private final RandomGenerator<T> generator;
	private final IntFunction<RandomGenerator<T>> supplier;

	public SupplyGeneratorArbitrary(IntFunction<RandomGenerator<T>> generatorSupplier) {
		this.supplier = generatorSupplier;
		this.generator = generatorSupplier.apply(1000); //TODO use real size
	}

	@Override
	public RandomGenerator<T> generator(final int genSize) {
		return supplier.apply(genSize);
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SupplyGeneratorArbitrary<?> that = (SupplyGeneratorArbitrary<?>) o;
		return LambdaSupport.areEqual(supplier, that.supplier);
	}

	@Override
	public int hashCode() {
		return supplier.getClass().hashCode();
	}
}
