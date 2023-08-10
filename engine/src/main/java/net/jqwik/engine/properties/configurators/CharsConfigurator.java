package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class CharsConfigurator extends ArbitraryConfiguratorBase {

	public StringArbitrary configure(StringArbitrary arbitrary, Chars chars) {
		return arbitrary.withChars(chars.value());
	}

	public StringArbitrary configure(StringArbitrary arbitrary, CharsList charsList) {
		for (Chars chars : charsList.value()) {
			arbitrary = configure(arbitrary, chars);
		}
		return arbitrary;
	}

	public StringArbitrary configure(StringArbitrary arbitrary, CharRange charRange) {
		return arbitrary.withCharRange(charRange.from(), charRange.to());
	}

	public StringArbitrary configure(StringArbitrary arbitrary, CharRangeList charRangeList) {
		for (CharRange charRange : charRangeList.value()) {
			arbitrary = configure(arbitrary, charRange);
		}
		return arbitrary;
	}

	public CharacterArbitrary configure(CharacterArbitrary arbitrary, CharRange range) {
		return arbitrary.range(range.from(), range.to());
	}

	public CharacterArbitrary configure(CharacterArbitrary arbitrary, CharRangeList charRangeList) {
		for (CharRange charRange : charRangeList.value()) {
			arbitrary = configure(arbitrary, charRange);
		}
		return arbitrary;
	}

}
