package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class StringLengthConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isOfType(String.class);
	}

	public Arbitrary<String> configure(Arbitrary<String> arbitrary, StringLength stringLength) {
		checkSize(stringLength);
		if (arbitrary instanceof StringArbitrary) {
			return configureStringArbitrary((StringArbitrary) arbitrary, stringLength);
		} else {
			return configureOtherArbitrary(arbitrary, stringLength);
		}
	}

	private Arbitrary<String> configureOtherArbitrary(Arbitrary<String> arbitrary, StringLength stringLength) {
		if (stringLength.value() != 0) {
			return arbitrary.filter(s -> s.length() == stringLength.value());
		} else {
			return arbitrary.filter(s -> s.length() >= stringLength.min() && s.length() <= stringLength.max());
		}
	}

	private Arbitrary<String> configureStringArbitrary(StringArbitrary stringArbitrary, StringLength stringLength) {
		if (stringLength.value() != 0) {
			return stringArbitrary.ofLength(stringLength.value());
		} else {
			StringArbitrary newArbitrary = stringArbitrary;
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
