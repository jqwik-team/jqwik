package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

abstract class NullableArbitraryProvider implements ArbitraryProvider {

	public Arbitrary<?> configure(NullableArbitrary<?> arbitrary, WithNull withNull) {
		if (withNull.target().isAssignableFrom(arbitrary.getTargetClass())) {
			return arbitrary.withNull(withNull.value());
		}
		return arbitrary;
	}

}
