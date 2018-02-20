package net.jqwik.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class WithNullConfigurator extends ArbitraryConfiguratorBase {

	public Arbitrary<?> configure(NullableArbitrary<?> arbitrary, WithNull withNull) {
		if (withNull.target().isAssignableFrom(arbitrary.getTargetClass())) {
			return arbitrary.withNull(withNull.value());
		}
		return arbitrary;
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, WithNull withNull) {
		if (arbitrary instanceof NullableArbitrary) {
			return arbitrary;
		}
		if (withNull.target() == Object.class) {
			return arbitrary.injectNull(withNull.value());
		}
		return arbitrary;
	}

}
