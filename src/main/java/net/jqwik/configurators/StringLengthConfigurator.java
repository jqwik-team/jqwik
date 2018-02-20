package net.jqwik.configurators;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class StringLengthConfigurator extends ArbitraryConfiguratorBase {

	public StringArbitrary configure(StringArbitrary arbitrary, StringLength stringLength) {
		return arbitrary.withMinLength(stringLength.min()).withMaxLength(stringLength.max());
	}

}
