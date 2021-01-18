package net.jqwik.time.internal.properties.configurators;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.api.constraints.*;

public class WithoutLeapYearsConfigurator extends ArbitraryConfiguratorBase {

	@Override
	protected boolean acceptTargetType(TypeUsage targetType) {
		return targetType.isAssignableFrom(LocalDate.class) || targetType.isAssignableFrom(YearMonth.class);
	}

	public Arbitrary<?> configure(Arbitrary<?> arbitrary, LeapYears leapYears) {
		if (arbitrary instanceof LocalDateArbitrary) {
			LocalDateArbitrary localDateArbitrary = (LocalDateArbitrary) arbitrary;
			return localDateArbitrary.leapYears(leapYears.withLeapYears());
		} else if (arbitrary instanceof YearMonthArbitrary) {
			YearMonthArbitrary yearMonthArbitrary = (YearMonthArbitrary) arbitrary;
			return yearMonthArbitrary.leapYears(leapYears.withLeapYears());
		} else {
			return arbitrary;
		}
	}

}
