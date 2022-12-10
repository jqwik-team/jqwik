package net.jqwik.time.api.dates.localDate;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
@PropertyDefaults(tries = 100)
public class DateMethodTests {

	@Provide
	Arbitrary<LocalDate> dates() {
		return Dates.dates();
	}

	@Group
	class DateMethods {

		@Property
		void atTheEarliest(@ForAll("dates") LocalDate startDate, @ForAll JqwikRandom random) {

			Arbitrary<LocalDate> dates = Dates.dates().atTheEarliest(startDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void atTheEarliestAtTheLatestMinAfterMax(
			@ForAll("dates") LocalDate startDate,
			@ForAll("dates") LocalDate endDate,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDate.isAfter(endDate));

			Arbitrary<LocalDate> dates = Dates.dates().atTheEarliest(startDate).atTheLatest(endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(endDate);
				assertThat(date).isBeforeOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void atTheLatest(@ForAll("dates") LocalDate endDate, @ForAll JqwikRandom random) {

			Arbitrary<LocalDate> dates = Dates.dates().atTheLatest(endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isBeforeOrEqualTo(endDate);
				return true;
			});

		}

		@Property
		void atTheLatestAtTheEarliestMinAfterMax(
			@ForAll("dates") LocalDate startDate,
			@ForAll("dates") LocalDate endDate,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDate.isAfter(endDate));

			Arbitrary<LocalDate> dates = Dates.dates().atTheLatest(endDate).atTheEarliest(startDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(endDate);
				assertThat(date).isBeforeOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void between(@ForAll("dates") LocalDate startDate, @ForAll("dates") LocalDate endDate, @ForAll JqwikRandom random) {

			Assume.that(!startDate.isAfter(endDate));

			Arbitrary<LocalDate> dates = Dates.dates().between(startDate, endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(startDate);
				assertThat(date).isBeforeOrEqualTo(endDate);
				return true;
			});
		}

		@Property
		void betweenEndDateBeforeStartDate(
			@ForAll("dates") LocalDate startDate,
			@ForAll("dates") LocalDate endDate,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDate.isAfter(endDate));

			Arbitrary<LocalDate> dates = Dates.dates().between(startDate, endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(endDate);
				assertThat(date).isBeforeOrEqualTo(startDate);
				return true;
			});
		}

		@Property
		void betweenSame(@ForAll("dates") LocalDate sameDate, @ForAll JqwikRandom random) {

			Arbitrary<LocalDate> dates = Dates.dates().between(sameDate, sameDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isEqualTo(sameDate);
				return true;
			});

		}

	}

	@Group
	class YearMethods {

		@Property
		void yearBetween(@ForAll("years") int startYear, @ForAll("years") int endYear, @ForAll JqwikRandom random) {

			Assume.that(startYear <= endYear);

			Arbitrary<LocalDate> dates = Dates.dates().yearBetween(startYear, endYear);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getYear()).isGreaterThanOrEqualTo(startYear);
				assertThat(date.getYear()).isLessThanOrEqualTo(endYear);
				return true;
			});

		}

		@Property
		void yearBetweenSame(@ForAll("years") int year, @ForAll JqwikRandom random) {

			Arbitrary<LocalDate> dates = Dates.dates().yearBetween(year, year);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getYear()).isEqualTo(year);
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

			Arbitrary<LocalDate> dates = Dates.dates().monthBetween(startMonth, endMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getMonth()).isGreaterThanOrEqualTo(of(startMonth));
				assertThat(date.getMonth()).isLessThanOrEqualTo(of(endMonth));
				return true;
			});

		}

		@Property
		void monthBetweenMinAfterMax(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll JqwikRandom random) {

			Assume.that(startMonth > endMonth);

			Arbitrary<LocalDate> dates = Dates.dates().monthBetween(startMonth, endMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getMonth()).isGreaterThanOrEqualTo(of(endMonth));
				assertThat(date.getMonth()).isLessThanOrEqualTo(of(startMonth));
				return true;
			});

		}

		@Property
		void monthBetweenSame(@ForAll("months") int month, @ForAll JqwikRandom random) {

			Arbitrary<LocalDate> dates = Dates.dates().monthBetween(month, month);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getMonth()).isEqualTo(of(month));
				return true;
			});

		}

		@Property
		void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll JqwikRandom random) {

			Arbitrary<LocalDate> dates = Dates.dates().onlyMonths(months.toArray(new Month[]{}));

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getMonth()).isIn(months);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> months() {
			return Arbitraries.integers().between(1, 12);
		}

	}

	@Group
	class DayOfMonthMethods {

		@Property
		void dayOfMonthBetween(
			@ForAll("dayOfMonths") int startDayOfMonth,
			@ForAll("dayOfMonths") int endDayOfMonth,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDayOfMonth <= endDayOfMonth);

			Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(startDayOfMonth);
				assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(endDayOfMonth);
				return true;
			});

		}

		@Property
		void dayOfMonthBetweenStartAfterEnd(
			@ForAll("dayOfMonths") int startDayOfMonth,
			@ForAll("dayOfMonths") int endDayOfMonth,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDayOfMonth > endDayOfMonth);

			Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(endDayOfMonth);
				assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(startDayOfMonth);
				return true;
			});

		}

		@Property
		void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth, @ForAll JqwikRandom random) {

			Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(dayOfMonth, dayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getDayOfMonth()).isEqualTo(dayOfMonth);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> dayOfMonths() {
			return Arbitraries.integers().between(1, 31);
		}

	}

	@Group
	class OnlyDaysOfWeekMethods {

		@Property
		void onlyDaysOfWeek(@ForAll @Size(min = 1) Set<DayOfWeek> dayOfWeeks, @ForAll JqwikRandom random) {

			Arbitrary<LocalDate> dates = Dates.dates().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.getDayOfWeek()).isIn(dayOfWeeks);
				return true;
			});
		}

	}

	@Group
	@PropertyDefaults(tries = 50)
	class MultipleCombinations {

		@Property
		void onlyFebruary29MoreThan8YearsBetween(
			@ForAll LocalDate min,
			@ForAll LocalDate max,
			@ForAll @IntRange(max = 2) int offset,
			@ForAll JqwikRandom random
		) {
			Assume.that(max.getYear() - min.getYear() >= 8);

			int minDayOfMonth = 29;
			int maxDayOfMonth = minDayOfMonth + offset;

			Arbitrary<LocalDate> dates = Dates.dates()
											  .between(min, max)
											  .onlyMonths(FEBRUARY)
											  .dayOfMonthBetween(minDayOfMonth, maxDayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isBetween(min, max);
				assertThat(date.getMonth()).isEqualTo(FEBRUARY);
				assertThat(date.getDayOfMonth()).isEqualTo(29);
				return true;
			});

		}

		@Property
		void onlyFebruary29With1To7YearsBetween(
			@ForAll LocalDate min,
			@ForAll MonthDay monthDay,
			@ForAll @IntRange(max = 7) int yearOffset,
			@ForAll @IntRange(max = 2) int dayOfMonthOffset,
			@ForAll JqwikRandom random
		) {

			Assume.that(
				yearOffset > 0
					|| min.getMonth().compareTo(monthDay.getMonth()) >= 0
					|| min.getDayOfMonth() <= monthDay.getDayOfMonth()
			);
			Assume.that(
				DefaultLocalDateArbitrary.isLeapYear(min.getYear() + yearOffset)
					|| !monthDay.equals(MonthDay.of(FEBRUARY, 29))
			);
			Assume.that(DefaultLocalDateArbitrary.leapYearPossible(min.getYear(), min.getYear() + yearOffset));
			Assume.that(!DefaultLocalDateArbitrary.isLeapYear(min.getYear()) || min.getMonth().compareTo(FEBRUARY) <= 0);
			Assume.that(
				!DefaultLocalDateArbitrary.isLeapYear(min.getYear() + yearOffset)
					|| monthDay.compareTo(MonthDay.of(FEBRUARY, 29)) >= 0
			);

			LocalDate max = LocalDate.of(min.getYear() + yearOffset, monthDay.getMonth(), monthDay.getDayOfMonth());

			int minDayOfMonth = 29;
			int maxDayOfMonth = minDayOfMonth + dayOfMonthOffset;

			Arbitrary<LocalDate> dates = Dates.dates()
											  .between(min, max)
											  .onlyMonths(FEBRUARY)
											  .dayOfMonthBetween(minDayOfMonth, maxDayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isBetween(min, max);
				assertThat(date.getMonth()).isEqualTo(FEBRUARY);
				assertThat(date.getDayOfMonth()).isEqualTo(29);
				return true;
			});

		}

	}

}
