package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;
import net.jqwik.time.internal.properties.arbitraries.valueRanges.*;

import static org.apiguardian.api.API.Status.*;

import static net.jqwik.time.internal.properties.arbitraries.valueRanges.PeriodBetween.*;

@API(status = INTERNAL)
public class DefaultPeriodArbitrary extends ArbitraryDecorator<Period> implements PeriodArbitrary {

	private final static Period DEFAULT_MIN = Period.ofYears(-1000);
	private final static Period DEFAULT_MAX = Period.ofYears(1000);

	private final PeriodBetween periodBetween = new PeriodBetween();

	@Override
	protected Arbitrary<Period> arbitrary() {

		Period min = periodBetween.getMin() != null ? periodBetween.getMin() : DEFAULT_MIN;
		Period max = periodBetween.getMax() != null ? periodBetween.getMax() : DEFAULT_MAX;

		long minInDays = inDays(min);
		long maxInDays = inDays(max);

		Arbitrary<Long> days = Arbitraries.longs()
										  .between(minInDays, maxInDays)
										  .edgeCases(edgeCases -> {
											  edgeCases.includeOnly(minInDays, 0L, maxInDays);
										  });

		return days.map(DefaultPeriodArbitrary::periodFromValue);

	}

	static private Period periodFromValue(long periodInDays) {
		int years = Math.toIntExact(periodInDays / DAYS_PER_YEAR);
		periodInDays %= DAYS_PER_YEAR;
		int months = Math.toIntExact(periodInDays / DAYS_PER_MONTH);
		int days = Math.toIntExact(periodInDays % DAYS_PER_MONTH);

		return Period.of(years, months, days);
	}

	@Override
	public PeriodArbitrary between(Period min, Period max) {
		DefaultPeriodArbitrary clone = typedClone();
		clone.periodBetween.set(min, max);
		return clone;
	}

}
