package net.jqwik.docs.arbitraryconfigurator;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

public class OddConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(Integer.class);
	}

	public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, Odd odd) {
		return arbitrary.filter(number  -> Math.abs(number % 2) == 1);
	}
}
