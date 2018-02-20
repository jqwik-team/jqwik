package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface NullableArbitrary<T> extends Arbitrary<T> {

	Class<?> getTargetClass();

	NullableArbitrary<T> withNull(double nullProbability);
}
