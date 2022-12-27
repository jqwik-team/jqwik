package net.jqwik.time.api.dates.period;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll JqwikRandom random) {
		PeriodArbitrary periods = Dates.periods();
		Period value = falsifyThenShrink(periods, random);
		assertThat(value).isEqualTo(Period.of(0, 0, 0));
	}

	@Property
	void shrinksToSmallestFailingValue(@ForAll JqwikRandom random) {
		PeriodArbitrary periods = Dates.periods();
		TestingFalsifier<Period> falsifier = period -> comparePeriodsWithMax30DaysAnd11Months(period, Period.of(200, 3, 5)) < 0;
		Period value = falsifyThenShrink(periods, random, falsifier);
		assertThat(value).isEqualTo(Period.of(200, 3, 5));
	}

	private int comparePeriodsWithMax30DaysAnd11Months(Period period1, Period period2) {
		if (period1.getYears() < period2.getYears()) {
			return -1;
		} else if (period2.getYears() < period1.getYears()) {
			return 1;
		}
		if (period1.getMonths() < period2.getMonths()) {
			return -1;
		} else if (period2.getMonths() < period1.getMonths()) {
			return 1;
		}
		if (period1.getDays() < period2.getDays()) {
			return -1;
		} else if (period2.getDays() < period1.getDays()) {
			return 1;
		} else {
			return 0;
		}
	}

}
