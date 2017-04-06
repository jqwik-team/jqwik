package net.jqwik.execution.properties.providers;

import java.util.function.*;

import javaslang.test.*;
import net.jqwik.api.properties.*;
import net.jqwik.execution.properties.*;

public class EnumArbitraryProvider implements TypedArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType, boolean withName) {
		return !withName && targetType.isEnum();
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeSupplier) {
		// noinspection unchecked
		return Generator.of((Class<Enum>) targetType.getRawType());
	}
}
