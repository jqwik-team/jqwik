package net.jqwik.configurators;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class StringLengthConfigurator extends ArbitraryConfiguratorBase {

	public StringArbitrary configure(StringArbitrary arbitrary, StringLength stringLength) {
		return arbitrary.ofMinLength(stringLength.min()).ofMaxLength(stringLength.max());
	}

}
