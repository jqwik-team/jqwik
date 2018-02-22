package net.jqwik.configurators;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class SizeConfigurator extends ArbitraryConfiguratorBase {

	public SizableArbitrary<?> configure(SizableArbitrary<?> arbitrary, Size size) {
		return arbitrary.ofMinSize(size.min()).ofMaxSize(size.max());
	}

}
