package net.jqwik.execution.providers;

import net.jqwik.api.*;
import net.jqwik.execution.*;

import java.util.*;
import java.util.function.*;

public interface ArbitraryProvider {

	boolean canProvideFor(GenericType targetType);

	Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider);
}
