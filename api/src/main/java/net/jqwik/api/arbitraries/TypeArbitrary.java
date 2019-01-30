package net.jqwik.api.arbitraries;

import java.lang.reflect.*;
import java.util.function.*;

import net.jqwik.api.*;

public interface TypeArbitrary<T> extends Arbitrary<T> {
	TypeArbitrary<T> use(Executable creator);

	TypeArbitrary<T> usePublicConstructors();

	TypeArbitrary<T> useAllConstructors();

	TypeArbitrary<T> useConstructors(Predicate<? super Constructor<?>> filter);

	TypeArbitrary<T> usePublicFactoryMethods();

	TypeArbitrary<T> useAllFactoryMethods();

	TypeArbitrary<T> useFactoryMethods(Predicate<Method> filter);
}
