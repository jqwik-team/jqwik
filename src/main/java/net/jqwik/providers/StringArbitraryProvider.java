package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;

public class StringArbitraryProvider extends NullableArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isOfType(String.class);
	}

	@Override
	public StringArbitrary provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.strings();
	}

	public StringArbitrary configure(StringArbitrary arbitrary, StringLength stringLength) {
		return arbitrary.withMinLength(stringLength.min()).withMaxLength(stringLength.max());
	}

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
