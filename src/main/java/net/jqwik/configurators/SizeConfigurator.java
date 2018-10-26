package net.jqwik.configurators;

import net.jqwik.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.properties.arbitraries.randomized.*;

public class SizeConfigurator extends ArbitraryConfiguratorBase {

	public SizableArbitrary<?> configure(SizableArbitrary<?> arbitrary, Size size) {
		checkSize(size);
		if (size.value() != 0) {
			return arbitrary.ofSize(size.value());
		} else {
			int effectiveMax = size.max() == 0 ? RandomGenerators.DEFAULT_COLLECTION_SIZE : size.max();
			return arbitrary.ofMinSize(size.min()).ofMaxSize(effectiveMax);
		}
	}

	private void checkSize(Size size) {
		if (size.value() == 0) {
			if (size.min() > size.max() && size.max() != 0) {
				reportError(size);
			}
		} else {
			if (size.min() != 0 || size.max() != 0)
				reportError(size);
		}
	}

	private void reportError(Size size) {
		throw new JqwikException(String.format("%s: You have to either choose a fixed value or set min and max", size));
	}

}
