package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultPeriodArbitrary extends ArbitraryDecorator<Period> implements PeriodArbitrary {

	private final static long DAYS_PER_MONTH = 31L; // The maximum
	private final static long DAYS_PER_YEAR = 372L; // 31 * 12

	private Period min = Period.ofYears(-1000);
	private Period max = Period.ofYears(1000);

	@Override
	protected Arbitrary<Period> arbitrary() {

		long minInDays = inDays(min);
		long maxInDays = inDays(max);

		Arbitrary<Long> days = Arbitraries.longs()
										  .between(minInDays, maxInDays)
										  .edgeCases(edgeCases -> {
											  edgeCases.includeOnly(minInDays, 0L, maxInDays);
										  });

		return days.map(this::calculatePeriod);

	}

	private Period calculatePeriod(long periodInDays) {
		int years = Math.toIntExact(periodInDays / DAYS_PER_YEAR);
		periodInDays %= DAYS_PER_YEAR;
		int months = Math.toIntExact(periodInDays / DAYS_PER_MONTH);
		int days = Math.toIntExact(periodInDays % DAYS_PER_MONTH);

		return Period.of(years, months, days);
	}

	private long inDays(Period period) {
		return period.getYears() * DAYS_PER_YEAR + period.getMonths() * DAYS_PER_MONTH + period.getDays();
	}

	@Override
	public PeriodArbitrary between(Period min, Period max) {
		if (inDays(min) > inDays(max)) {
			Period remember = min;
			min = max;
			max = remember;
		}
		DefaultPeriodArbitrary clone = typedClone();
		clone.min = min;
		clone.max = max;
		return clone;
	}

}
