package net.jqwik.engine.properties.configurators;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class WhitespaceConfigurator extends ArbitraryConfiguratorBase {

	public StringArbitrary configure(StringArbitrary arbitrary, Whitespace whitespace) {
		return arbitrary.whitespace();
	}

	public CharacterArbitrary configure(CharacterArbitrary arbitrary, Whitespace whitespace) {
		return arbitrary.whitespace();
	}

}
