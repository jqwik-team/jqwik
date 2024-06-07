package net.jqwik.engine.properties.arbitraries;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;

import org.jspecify.annotations.*;

public class FromGeneratorWithSizeArbitrary<T extends @Nullable Object> implements Arbitrary<T> {

	private final IntFunction<? extends RandomGenerator<T>> supplier;

	public FromGeneratorWithSizeArbitrary(IntFunction<? extends RandomGenerator<T>> generatorSupplier) {
		this.supplier = generatorSupplier;
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

		FromGeneratorWithSizeArbitrary<?> that = (FromGeneratorWithSizeArbitrary<?>) o;
		return LambdaSupport.areEqual(supplier, that.supplier);
	}

	@Override
	public int hashCode() {
		return supplier.getClass().hashCode();
	}
}
