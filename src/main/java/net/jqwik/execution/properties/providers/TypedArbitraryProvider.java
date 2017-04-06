package net.jqwik.execution.properties.providers;

import java.util.function.*;

import javaslang.test.*;
import net.jqwik.execution.properties.*;

public interface TypedArbitraryProvider {

	boolean canProvideFor(GenericType targetType, boolean withName);

	Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Arbitrary<?>> subtypeProvider);
}
