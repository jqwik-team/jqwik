package net.jqwik.time.api.dates.period;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class PeriodMethodsTests {

	@Property
	void between(@ForAll int start, @ForAll int end, @ForAll JqwikRandom random) {
		Assume.that(start <= end);

		Arbitrary<Period> periods = Dates.periods().between(Period.ofYears(start), Period.ofYears(end));

		assertAllGenerated(periods.generator(1000), random, period -> {
			assertThat(period.getYears()).isBetween(start, end);
		});

	}

	@Property
	void betweenStartPeriodAfterEndPeriod(@ForAll int start, @ForAll int end, @ForAll JqwikRandom random) {
		Assume.that(start > end);

		Arbitrary<Period> periods = Dates.periods().between(Period.ofYears(start), Period.ofYears(end));

		assertAllGenerated(periods.generator(1000), random, period -> {
			assertThat(period.getYears()).isBetween(end, start);
		});

	}

	@Example
	void test() {
		onlyOnePeriodPossible(0, 0, 1, new Random(42L));
	}

	@Property
	void onlyOnePeriodPossible(
		@ForAll @IntRange(min = 0, max = Integer.MAX_VALUE) int year,
		@ForAll @IntRange(min = 0, max = 11) int month,
		@ForAll @IntRange(min = 0, max = 30) int day,
		@ForAll JqwikRandom random
	) {

		Period minMax = Period.of(year, month, day);
		Arbitrary<Period> periods = Dates.periods().between(minMax, minMax);

		assertAllGenerated(periods.generator(1000), random, period -> {
			assertThat(period.getYears()).isEqualTo(year);
			assertThat(period.getMonths()).isEqualTo(month);
			assertThat(period.getDays()).isEqualTo(day);
		});

	}

}
