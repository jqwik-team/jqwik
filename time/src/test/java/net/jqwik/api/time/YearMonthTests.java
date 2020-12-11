package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.testing.TestingSupport.*;

@Group
class YearMonthTests {

	@Property
	void validYearMonthIsGenerated(@ForAll("yearMonths") YearMonth yearMonth) {
		assertThat(yearMonth).isNotNull();
	}

	@Provide
	Arbitrary<YearMonth> yearMonths() {
		return Dates.yearMonths();
	}

	@Group
	class CheckYearMonthMethods {

		@Group
		class YearMonthMethods {

			@Property
			void atTheEarliest(@ForAll("yearMonths") YearMonth yearMonth, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().atTheEarliest(yearMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym).isGreaterThanOrEqualTo(yearMonth);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("yearMonths") YearMonth yearMonth, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().atTheLatest(yearMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym).isLessThanOrEqualTo(yearMonth);
					return true;
				});

			}

			@Property
			void between(@ForAll("yearMonths") YearMonth startYearMonth, @ForAll("yearMonths") YearMonth endYearMonth, @ForAll Random random) {

				Assume.that(!startYearMonth.isAfter(endYearMonth));

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().between(startYearMonth, endYearMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym).isGreaterThanOrEqualTo(startYearMonth);
					assertThat(ym).isLessThanOrEqualTo(endYearMonth);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("yearMonths") YearMonth yearMonth, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().between(yearMonth, yearMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym).isEqualTo(yearMonth);
					return true;
				});

			}

		}

		@Group
		class YearMethods {

			@Property
			void yearGreaterOrEqual(@ForAll("years") int year, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().yearGreaterOrEqual(year);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getYear()).isGreaterThanOrEqualTo(year);
					return true;
				});

			}

			@Property
			void yearLessOrEqual(@ForAll("years") int year, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().yearLessOrEqual(year);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getYear()).isLessThanOrEqualTo(year);
					return true;
				});

			}

			@Property
			void yearBetween(@ForAll("years") int startYear, @ForAll("years") int endYear, @ForAll Random random) {

				Assume.that(startYear <= endYear);

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().yearBetween(startYear, endYear);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getYear()).isGreaterThanOrEqualTo(startYear);
					assertThat(ym.getYear()).isLessThanOrEqualTo(endYear);
					return true;
				});

			}

			@Property
			void yearBetweenSame(@ForAll("years") int year, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().yearBetween(year, year);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getYear()).isEqualTo(year);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> years() {
				return Arbitraries.integers().between(LocalDate.MIN.getYear(), LocalDate.MAX.getYear());
			}

		}

		@Group
		class MonthMethods {

			@Property
			void monthGreaterOrEqual(@ForAll("months") int month, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthGreaterOrEqual(month);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getMonth()).isGreaterThanOrEqualTo(Month.of(month));
					return true;
				});

			}

			@Property
			void monthLessOrEqual(@ForAll("months") int month, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthLessOrEqual(month);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getMonth()).isLessThanOrEqualTo(Month.of(month));
					return true;
				});

			}

			@Property
			void monthBetween(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll Random random) {

				Assume.that(startMonth <= endMonth);

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthBetween(startMonth, endMonth);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getMonth()).isGreaterThanOrEqualTo(Month.of(startMonth));
					assertThat(ym.getMonth()).isLessThanOrEqualTo(Month.of(endMonth));
					return true;
				});

			}

			@Property
			void monthBetweenSame(@ForAll("months") int month, @ForAll Random random) {

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().monthBetween(month, month);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getMonth()).isEqualTo(Month.of(month));
					return true;
				});

			}

			@Property
			void monthOnlyMonths(@ForAll("monthsOnlyMonths") Month[] months, @ForAll Random random){

				Arbitrary<YearMonth> yearMonths = Dates.yearMonths().onlyMonths(months);

				assertAllGenerated(yearMonths.generator(1000), random, ym -> {
					assertThat(ym.getMonth()).isIn(months);
					return true;
				});

			}

			@Provide
			Arbitrary<Month[]> monthsOnlyMonths(){
				return MonthTests.generateMonths();
			}

			@Provide
			Arbitrary<Integer> months() {
				return Arbitraries.integers().between(1, 12);
			}

		}

	}

}
