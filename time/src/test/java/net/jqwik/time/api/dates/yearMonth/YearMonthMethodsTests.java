package net.jqwik.time.api.dates.yearMonth;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
@PropertyDefaults(tries = 100)
public class YearMonthMethodsTests {

	@Provide
	Arbitrary<YearMonth> yearMonths() {
		return Dates.yearMonths();
	}

	@Group
	class YearMonthMethods {

		@Property
		void atTheEarliest(@ForAll("yearMonths") YearMonth yearMonth, @ForAll JqwikRandom random) {

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().atTheEarliest(yearMonth);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym).isGreaterThanOrEqualTo(yearMonth);
				return true;
			});

		}

		@Property
		void atTheEarliestAtTheLatestMinAfterMax(
			@ForAll("yearMonths") YearMonth min,
			@ForAll("yearMonths") YearMonth max,
			@ForAll JqwikRandom random
		) {

			Assume.that(min.isAfter(max));

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().atTheEarliest(min).atTheLatest(max);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym).isGreaterThanOrEqualTo(max);
				assertThat(ym).isLessThanOrEqualTo(min);
				return true;
			});

		}

		@Property
		void atTheLatest(@ForAll("yearMonths") YearMonth yearMonth, @ForAll JqwikRandom random) {

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().atTheLatest(yearMonth);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym).isLessThanOrEqualTo(yearMonth);
				return true;
			});

		}

		@Property
		void atTheLatestAtTheEarliestMinAfterMax(
			@ForAll("yearMonths") YearMonth min,
			@ForAll("yearMonths") YearMonth max,
			@ForAll JqwikRandom random
		) {

			Assume.that(min.isAfter(max));

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().atTheLatest(max).atTheEarliest(min);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym).isGreaterThanOrEqualTo(max);
				assertThat(ym).isLessThanOrEqualTo(min);
				return true;
			});

		}

		@Property
		void between(
			@ForAll("yearMonths") YearMonth startYearMonth,
			@ForAll("yearMonths") YearMonth endYearMonth,
			@ForAll JqwikRandom random
		) {

			Assume.that(!startYearMonth.isAfter(endYearMonth));

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().between(startYearMonth, endYearMonth);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym).isGreaterThanOrEqualTo(startYearMonth);
				assertThat(ym).isLessThanOrEqualTo(endYearMonth);
				return true;
			});
		}

		@Property
		void betweenSame(@ForAll("yearMonths") YearMonth yearMonth, @ForAll JqwikRandom random) {

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().between(yearMonth, yearMonth);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym).isEqualTo(yearMonth);
				return true;
			});

		}

	}

	@Group
	class YearMethods {

		@Property
		void yearBetween(@ForAll("years") int startYear, @ForAll("years") int endYear, @ForAll JqwikRandom random) {

			Assume.that(startYear <= endYear);

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().yearBetween(startYear, endYear);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym.getYear()).isGreaterThanOrEqualTo(startYear);
				assertThat(ym.getYear()).isLessThanOrEqualTo(endYear);
				return true;
			});

		}

		@Property
		void yearBetweenSame(@ForAll("years") int year, @ForAll JqwikRandom random) {

			Assume.that(year != 0);

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().yearBetween(year, year);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym.getYear()).isEqualTo(year);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> years() {
			return Arbitraries.integers().between(1, LocalDate.MAX.getYear());
		}

	}

	@Group
	class MonthMethods {

		@Property
		void monthBetween(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll JqwikRandom random) {

			Assume.that(startMonth <= endMonth);

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthBetween(startMonth, endMonth);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym.getMonth()).isGreaterThanOrEqualTo(Month.of(startMonth));
				assertThat(ym.getMonth()).isLessThanOrEqualTo(Month.of(endMonth));
				return true;
			});

		}

		@Property
		void monthBetweenMinAfterMax(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll JqwikRandom random) {

			Assume.that(startMonth > endMonth);

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthBetween(startMonth, endMonth);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym.getMonth()).isGreaterThanOrEqualTo(Month.of(endMonth));
				assertThat(ym.getMonth()).isLessThanOrEqualTo(Month.of(startMonth));
				return true;
			});

		}

		@Property
		void monthBetweenSame(@ForAll("months") int month, @ForAll JqwikRandom random) {

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthBetween(month, month);

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym.getMonth()).isEqualTo(Month.of(month));
				return true;
			});

		}

		@Property
		void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll JqwikRandom random) {

			Arbitrary<YearMonth> yearMonths = Dates.yearMonths().onlyMonths(months.toArray(new Month[]{}));

			checkAllGenerated(yearMonths.generator(1000, true), random, ym -> {
				assertThat(ym.getMonth()).isIn(months);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> months() {
			return Arbitraries.integers().between(1, 12);
		}

	}

}
