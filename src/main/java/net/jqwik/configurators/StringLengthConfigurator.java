package net.jqwik.configurators;

import net.jqwik.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.properties.arbitraries.randomized.*;

public class StringLengthConfigurator extends ArbitraryConfiguratorBase {

	public StringArbitrary configure(StringArbitrary arbitrary, StringLength stringLength) {
		checkSize(stringLength);
		if (stringLength.value() != 0) {
			return arbitrary.ofLength(stringLength.value());
		} else {
			int effectiveMax = stringLength.max() == 0 ? RandomGenerators.DEFAULT_COLLECTION_SIZE : stringLength.max();
			return arbitrary.ofMinLength(stringLength.min()).ofMaxLength(effectiveMax);
		}
	}

	private void checkSize(StringLength stringLength) {
		if (stringLength.value() == 0) {
			if (stringLength.min() > stringLength.max() && stringLength.max() != 0)
				reportError(stringLength);
		} else {
			if (stringLength.min() != 0 || stringLength.max() != 0)
				reportError(stringLength);
		}
	}

	private void reportError(StringLength stringLength) {
		throw new JqwikException(String.format("%s: You have to either choose a fixed value or set min/max", stringLength));
	}

}
