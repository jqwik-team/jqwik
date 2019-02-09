package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class StringLengthConfigurator extends ArbitraryConfiguratorBase {

	public StringArbitrary configure(StringArbitrary arbitrary, StringLength stringLength) {
		checkSize(stringLength);
		if (stringLength.value() != 0) {
			return arbitrary.ofLength(stringLength.value());
		} else {
			StringArbitrary newArbitrary = arbitrary;
			if (stringLength.min() != 0) {
				newArbitrary = newArbitrary.ofMinLength(stringLength.min());
			}
			if (stringLength.max() != 0) {
				newArbitrary = newArbitrary.ofMaxLength(stringLength.max());
			}
			return newArbitrary;
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
