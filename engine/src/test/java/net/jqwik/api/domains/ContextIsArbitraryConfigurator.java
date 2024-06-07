package net.jqwik.api.domains;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

import org.jspecify.annotations.*;

class ContextIsArbitraryConfigurator extends DomainContextBase implements ArbitraryConfigurator {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends @Nullable Object> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
		if (targetType.isOfType(String.class) && targetType.isAnnotated(Doubled.class)) {
			Arbitrary<String> stringArbitrary = (Arbitrary<String>) arbitrary;
			return (Arbitrary<T>) stringArbitrary.map(s -> s + s);
		}
		return arbitrary;
	}

}
