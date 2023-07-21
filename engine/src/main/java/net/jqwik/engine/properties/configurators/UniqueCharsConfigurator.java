package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

@SuppressWarnings("unchecked")
public class UniqueCharsConfigurator implements ArbitraryConfigurator {

	@Override
	public <T> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
		return targetType.findAnnotation(UniqueChars.class).map(uniqueness -> {
			if (arbitrary instanceof StringArbitrary) {
				return (Arbitrary<T>) ((StringArbitrary) arbitrary).uniqueChars();
			}
			if (targetType.isAssignableFrom(String.class)) {
				Arbitrary<String> stringArbitrary = (Arbitrary<String>) arbitrary;
				return (Arbitrary<T>) stringArbitrary.filter(string -> hasUniqueChars(string));
			}
			return arbitrary;
		}).orElse(arbitrary);
	}

	private boolean hasUniqueChars(String string) {
		return string.chars().distinct().count() == string.length();
	}

}
