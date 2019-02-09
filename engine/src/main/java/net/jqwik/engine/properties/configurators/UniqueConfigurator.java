package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class UniqueConfigurator extends ArbitraryConfiguratorBase {

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, Unique unique) {
		return arbitrary.unique();
	}

	@Override
	public int order() {
		// Apply later than standard configurators with order 100
		return 1000;
	}
}
