package net.jqwik.execution.properties.providers;

import java.util.function.*;

import javaslang.test.*;
import net.jqwik.execution.properties.*;

public class IntegerArbitraryProvider implements TypedArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType, boolean withName) {
		return !withName && targetType.isAssignableFrom(Integer.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		return Arbitrary.integer();
	}
}
