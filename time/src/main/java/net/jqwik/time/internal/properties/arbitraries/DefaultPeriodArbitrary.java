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

	private int monthsMin = 0;
	private int monthsMax = 11;

	private int daysMin = 0;
	private int daysMax = 30;

	@Override
	protected Arbitrary<Period> arbitrary() {

		IntegerArbitrary years = Arbitraries.integers().between(yearsMin, yearsMax);
		IntegerArbitrary months = Arbitraries.integers().between(monthsMin, monthsMax);
		IntegerArbitrary days = Arbitraries.integers().between(daysMin, daysMax);

		Arbitrary<Period> periodArbitrary = Combinators.combine(years, months, days).as(Period::of);

		periodArbitrary = periodArbitrary.edgeCases(edgeCases -> {
			edgeCases.includeOnly(Period.of(yearsMin, monthsMin, daysMin), Period.of(yearsMax, monthsMax, daysMax));
			if (yearsMin <= 0 && yearsMax >= 0 && monthsMin <= 0 && monthsMax >= 0 && daysMin <= 0 && daysMax >= 0) {
				edgeCases.add(Period.of(0, 0, 0));
			}
		});

		return periodArbitrary;

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
