package net.jqwik.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.time.*;

public class DefaultDateArbitrary extends ArbitraryDecorator<LocalDate> implements DateArbitrary {

	@Override
	protected Arbitrary<LocalDate> arbitrary() {
		return Arbitraries.just(LocalDate.of(2020, 12, 9));
	}

}
