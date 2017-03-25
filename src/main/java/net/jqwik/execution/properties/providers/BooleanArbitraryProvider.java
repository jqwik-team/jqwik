package net.jqwik.execution.properties.providers;

import javaslang.test.Arbitrary;
import net.jqwik.execution.properties.GenericType;

import java.util.function.Function;

public class BooleanArbitraryProvider implements TypedArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType, boolean withName) {
		return !withName && targetType.isAssignableFrom(Boolean.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		return Arbitrary.of(true, false);
	}
}
