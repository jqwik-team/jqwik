package net.jqwik.engine.properties.configurators;

import net.jqwik.api.arbitraries.CharacterArbitrary;
import net.jqwik.api.arbitraries.StringArbitrary;
import net.jqwik.api.configurators.ArbitraryConfiguratorBase;
import net.jqwik.api.constraints.Whitespace;
import net.jqwik.engine.properties.arbitraries.DefaultStringArbitrary;

public class WhitespaceConfigurator extends ArbitraryConfiguratorBase {

	public StringArbitrary configure(StringArbitrary arbitrary, Whitespace whitespace) {
		return arbitrary.withChars(DefaultStringArbitrary.WHITESPACE_CHARS);
	}

	public CharacterArbitrary configure(CharacterArbitrary arbitrary, Whitespace whitespace) {
		CharacterArbitrary result = arbitrary;

		for (char c : DefaultStringArbitrary.WHITESPACE_CHARS) {
			result = result.between(c, c);
		}

		return result;
	}

}
