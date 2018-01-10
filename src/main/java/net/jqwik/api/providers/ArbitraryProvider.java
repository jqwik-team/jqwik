package net.jqwik.api.providers;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

public interface ArbitraryProvider {

	boolean canProvideFor(GenericType targetType);

	Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider);
}
