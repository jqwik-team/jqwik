package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import static net.jqwik.api.arbitraries.CharacterArbitrary.*;

public class NotBlankConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isOfType(String.class);
	}

	public Arbitrary<String> configure(Arbitrary<String> arbitrary, NotBlank notBlank) {
		return arbitrary.filter(s -> s != null && !isBlank(s.trim()));
	}
}
