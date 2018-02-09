package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface TargetableArbitrary<T> extends Arbitrary<T> {

	Class<?> getTargetClass();
}
