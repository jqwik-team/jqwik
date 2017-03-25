package net.jqwik.execution.properties;

import javaslang.test.Arbitrary;

import java.util.function.Function;

public interface TypedArbitraryProvider {

	boolean canProvideFor(GenericType providerType);

	Arbitrary<?> provideFor(GenericType providerType, Function<GenericType, Arbitrary<?>> subtypeSupplier);
}
