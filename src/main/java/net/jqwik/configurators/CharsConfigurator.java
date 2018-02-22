package net.jqwik.configurators;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class CharsConfigurator extends ArbitraryConfiguratorBase {

	public StringArbitrary configure(StringArbitrary arbitrary, Chars chars) {
		return arbitrary.withChars(chars.value()).withChars(chars.from(), chars.to());
	}

	public StringArbitrary configure(StringArbitrary arbitrary, CharsList charsList) {
		for (Chars chars : charsList.value()) {
			arbitrary = configure(arbitrary, chars);
		}
		return arbitrary;
	}

}
