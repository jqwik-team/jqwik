package net.jqwik.engine.properties.arbitraries;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;

public class CreateArbitrary<T> extends FromGeneratorsArbitrary<T> {

	private final Supplier<T> supplier;

	public CreateArbitrary(Supplier<T> supplier) {
		super(
			random -> Shrinkable.supplyUnshrinkable(supplier),
			max -> ExhaustiveGenerators.create(supplier, max),
			maxEdgeCases -> EdgeCases.fromSupplier(() -> Shrinkable.supplyUnshrinkable(supplier))
		);
		this.supplier = supplier;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CreateArbitrary<?> that = (CreateArbitrary<?>) o;
		return LambdaSupport.areEqual(supplier, that.supplier);
	}

	@Override
	public int hashCode() {
		return supplier.getClass().hashCode();
	}
}
