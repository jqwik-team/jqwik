package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

public class UniqueCharsConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.canBeAssignedTo(TypeUsage.of(CharSequence.class));
	}

	public Arbitrary<? extends CharSequence> configure(Arbitrary<? extends CharSequence> arbitrary, UniqueChars uniqueChars) {
		if (arbitrary instanceof StringArbitrary) {
			return ((StringArbitrary) arbitrary).uniqueChars();
		} else {
			return arbitrary.filter(this::hasUniqueChars);
		}
	}

	private boolean hasUniqueChars(CharSequence charSequence) {
		return charSequence.chars().distinct().count() == charSequence.length();
	}

}
