package net.jqwik.execution.properties.providers;

import net.jqwik.execution.properties.*;
import net.jqwik.properties.*;

import java.util.function.*;

public interface TypedArbitraryProvider {

	boolean canProvideFor(GenericType targetType, boolean withName);

	Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeProvider);
}
