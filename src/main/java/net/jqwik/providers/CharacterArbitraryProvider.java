package net.jqwik.providers;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class CharacterArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isCompatibleWith(Character.class);
	}

	@Override
	public CharacterArbitrary provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.chars();
	}

	public CharacterArbitrary configure(CharacterArbitrary arbitrary, Chars chars) {
		return arbitrary.withChars(chars.value()).withChars(chars.from(), chars.to());
	}

	public CharacterArbitrary configure(CharacterArbitrary arbitrary, CharsList charsList) {
		for (Chars chars : charsList.value()) {
			arbitrary = configure(arbitrary, chars);
		}
		return arbitrary;
	}

}
