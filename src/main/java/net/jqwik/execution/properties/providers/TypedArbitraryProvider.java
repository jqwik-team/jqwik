package net.jqwik.execution.properties.providers;

import javaslang.test.Arbitrary;
import net.jqwik.execution.properties.GenericType;

import java.util.function.Function;

public interface TypedArbitraryProvider {

	boolean canProvideFor(GenericType targetType);

	Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeProvider);
}
