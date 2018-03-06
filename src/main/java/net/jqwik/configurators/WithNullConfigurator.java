package net.jqwik.configurators;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class WithNullConfigurator extends ArbitraryConfiguratorBase {

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, WithNull withNull) {
		return arbitrary.injectNull(withNull.value());
	}

}
