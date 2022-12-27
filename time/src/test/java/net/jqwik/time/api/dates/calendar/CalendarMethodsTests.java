package net.jqwik.time.api.dates.calendar;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
@PropertyDefaults(tries = 100)
public class CalendarMethodsTests {

	@Provide
	Arbitrary<Calendar> dates() {
		return Dates.datesAsCalendar();
	}

	@Group
	class CalendarMethods {

		@Property
		void atTheEarliest(@ForAll("dates") Calendar startDate, @ForAll JqwikRandom random) {

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().atTheEarliest(startDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isGreaterThanOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void atTheEarliestAtTheLatestMinAfterMax(
			@ForAll("dates") Calendar startDate,
			@ForAll("dates") Calendar endDate,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDate.after(endDate));

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().atTheEarliest(startDate).atTheLatest(endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isGreaterThanOrEqualTo(endDate);
				assertThat(date).isLessThanOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void atTheLatest(@ForAll("dates") Calendar endDate, @ForAll JqwikRandom random) {

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().atTheLatest(endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isLessThanOrEqualTo(endDate);
				return true;
			});

		}

		@Property
		void atTheLatestAtTheEarliestMinAfterMax(
			@ForAll("dates") Calendar startDate,
			@ForAll("dates") Calendar endDate,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDate.after(endDate));

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().atTheLatest(endDate).atTheEarliest(startDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isGreaterThanOrEqualTo(endDate);
				assertThat(date).isLessThanOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void between(@ForAll("dates") Calendar startDate, @ForAll("dates") Calendar endDate, @ForAll JqwikRandom random) {

			Assume.that(!startDate.after(endDate));

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().between(startDate, endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isGreaterThanOrEqualTo(startDate);
				assertThat(date).isLessThanOrEqualTo(endDate);
				return true;
			});
		}

		@Property
		void betweenEndDateAfterStartDate(
			@ForAll("dates") Calendar startDate,
			@ForAll("dates") Calendar endDate,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDate.after(endDate));

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().between(startDate, endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isGreaterThanOrEqualTo(endDate);
				assertThat(date).isLessThanOrEqualTo(startDate);
				return true;
			});
		}

		@Property
		void betweenSame(@ForAll("dates") Calendar sameDate, @ForAll JqwikRandom random) {

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().between(sameDate, sameDate);

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

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().yearBetween(startYear, endYear);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.get(Calendar.YEAR)).isGreaterThanOrEqualTo(startYear);
				assertThat(date.get(Calendar.YEAR)).isLessThanOrEqualTo(endYear);
				return true;
			});

		}

		@Property
		void yearBetweenSame(@ForAll("years") int year, @ForAll JqwikRandom random) {

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().yearBetween(year, year);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.get(Calendar.YEAR)).isEqualTo(year);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> years() {
			return Arbitraries.integers().between(1, 292_278_993); //Maximum Calendar.YEAR value is 292_278_993
		}

	}

	@Group
	class MonthMethods {

		@Property
		void monthBetween(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll JqwikRandom random) {

			Assume.that(startMonth <= endMonth);

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().monthBetween(startMonth, endMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isGreaterThanOrEqualTo(Month.of(startMonth));
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isLessThanOrEqualTo(Month.of(endMonth));
				return true;
			});

		}

		@Property
		void monthBetweenMinAfterMax(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll JqwikRandom random) {

			Assume.that(startMonth > endMonth);

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().monthBetween(startMonth, endMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isGreaterThanOrEqualTo(Month.of(endMonth));
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isLessThanOrEqualTo(Month.of(startMonth));
				return true;
			});

		}

		@Property
		void monthBetweenSame(@ForAll("months") int month, @ForAll JqwikRandom random) {

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().monthBetween(month, month);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isEqualTo(Month.of(month));
				return true;
			});

		}

		@Property
		void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll JqwikRandom random) {

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().onlyMonths(months.toArray(new Month[]{}));

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(date)).isIn(months);
				return true;
			});

		}

		@Provide
		Arbitrary<Integer> months() {
			return Arbitraries.integers().between(1, 12);
		}

	}

	@Group
	@PropertyDefaults(tries = 100)
	class DayOfMonthMethods {

		@Property
		void dayOfMonthBetween(
			@ForAll("dayOfMonths") int startDayOfMonth,
			@ForAll("dayOfMonths") int endDayOfMonth,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDayOfMonth <= endDayOfMonth);

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(startDayOfMonth);
				assertThat(date.get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(endDayOfMonth);
				return true;
			});

		}

		@Property
		void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth, @ForAll JqwikRandom random) {

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().dayOfMonthBetween(dayOfMonth, dayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date.get(Calendar.DAY_OF_MONTH)).isEqualTo(dayOfMonth);
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

			Arbitrary<Calendar> dates = Dates.datesAsCalendar().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(date)).isIn(dayOfWeeks);
				return true;
			});
		}

	}

}
