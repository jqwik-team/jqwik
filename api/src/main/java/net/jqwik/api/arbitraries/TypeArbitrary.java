package net.jqwik.api.arbitraries;

import java.lang.reflect.*;

import net.jqwik.api.*;

public interface TypeArbitrary<T> extends Arbitrary<T> {
	TypeArbitrary<T> use(Executable creator);

	TypeArbitrary<T> usePublicConstructors();
}
