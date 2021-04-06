package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultYearArbitrary extends ArbitraryDecorator<Year> implements YearArbitrary {

	private static final Year DEFAULT_MIN = Year.of(1900);
	private static final Year DEFAULT_MAX = Year.of(2500);

	private final YearBetween yearBetween = new YearBetween().allowBelow0();

	@Override
	protected Arbitrary<Year> arbitrary() {
		Year min = yearBetween.getMin() != null ? yearBetween.getMin() : DEFAULT_MIN;
		Year max = yearBetween.getMax() != null ? yearBetween.getMax() : DEFAULT_MAX;
		Arbitrary<Integer> years = Arbitraries.integers().between(min.getValue(), max.getValue()).filter(v -> v != 0);
		return years.map(Year::of);
	}

	@Override
	public YearArbitrary between(Year min, Year max) {
		DefaultYearArbitrary clone = typedClone();
		clone.yearBetween.set(min, max);
		return clone;
	}

}
