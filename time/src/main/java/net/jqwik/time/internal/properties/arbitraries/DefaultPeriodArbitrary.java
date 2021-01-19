package net.jqwik.time.internal.properties.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class DefaultPeriodArbitrary extends ArbitraryDecorator<Period> implements PeriodArbitrary {

	private int yearsMin = Integer.MIN_VALUE;
	private int yearsMax = Integer.MAX_VALUE;

	private int monthsMin = Integer.MIN_VALUE;
	private int monthsMax = Integer.MAX_VALUE;

	private int daysMin = Integer.MIN_VALUE;
	private int daysMax = Integer.MAX_VALUE;

	@Override
	protected Arbitrary<Period> arbitrary() {

		IntegerArbitrary years = Arbitraries.integers().between(yearsMin, yearsMax);
		IntegerArbitrary months = Arbitraries.integers().between(monthsMin, monthsMax);
		IntegerArbitrary days = Arbitraries.integers().between(daysMin, daysMax);

		return Combinators.combine(years, months, days).as((y, m, d) -> Period.ofYears(y).plusMonths(m).plusDays(d));

	}

	@Override
	public PeriodArbitrary yearsBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}
		DefaultPeriodArbitrary clone = typedClone();
		clone.yearsMin = min;
		clone.yearsMax = max;
		return clone;
	}

	@Override
	public PeriodArbitrary monthsBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}
		DefaultPeriodArbitrary clone = typedClone();
		clone.monthsMin = min;
		clone.monthsMax = max;
		return clone;
	}

	@Override
	public PeriodArbitrary daysBetween(int min, int max) {
		if (min > max) {
			int remember = min;
			min = max;
			max = remember;
		}
		DefaultPeriodArbitrary clone = typedClone();
		clone.daysMin = min;
		clone.daysMax = max;
		return clone;
	}

}
