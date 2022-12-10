package net.jqwik.time.api.dates.date;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;
import static net.jqwik.time.api.testingSupport.ForDate.*;

@Group
@PropertyDefaults(tries = 100)
public class DateMethodsTests {

	@Provide
	Arbitrary<Date> dates() {
		return Dates.datesAsDate();
	}

	@Group
	class DateMethods {

		@Property
		void atTheEarliest(@ForAll("dates") Date startDate, @ForAll JqwikRandom random) {

			Arbitrary<Date> dates = Dates.datesAsDate().atTheEarliest(startDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void atTheEarliestAtTheLatestMinAfterMax(
			@ForAll("dates") Date startDate,
			@ForAll("dates") Date endDate,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDate.after(endDate));

			Arbitrary<Date> dates = Dates.datesAsDate().atTheEarliest(startDate).atTheLatest(endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(endDate);
				assertThat(date).isBeforeOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void atTheLatest(@ForAll("dates") Date endDate, @ForAll JqwikRandom random) {

			Arbitrary<Date> dates = Dates.datesAsDate().atTheLatest(endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isBeforeOrEqualTo(endDate);
				return true;
			});

		}

		@Property
		void atTheLatestAtTheEarliestMinAfterMax(
			@ForAll("dates") Date startDate,
			@ForAll("dates") Date endDate,
			@ForAll JqwikRandom random
		) {

			Assume.that(startDate.after(endDate));

			Arbitrary<Date> dates = Dates.datesAsDate().atTheLatest(endDate).atTheEarliest(startDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(endDate);
				assertThat(date).isBeforeOrEqualTo(startDate);
				return true;
			});

		}

		@Property
		void between(@ForAll("dates") Date startDate, @ForAll("dates") Date endDate, @ForAll JqwikRandom random) {

			Assume.that(!startDate.after(endDate));

			Arbitrary<Date> dates = Dates.datesAsDate().between(startDate, endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(startDate);
				assertThat(date).isBeforeOrEqualTo(endDate);
				return true;
			});
		}

		@Property
		void betweenEndDateBeforeStartDate(@ForAll("dates") Date startDate, @ForAll("dates") Date endDate, @ForAll JqwikRandom random) {

			Assume.that(startDate.after(endDate));

			Arbitrary<Date> dates = Dates.datesAsDate().between(startDate, endDate);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(date).isAfterOrEqualTo(endDate);
				assertThat(date).isBeforeOrEqualTo(startDate);
				return true;
			});
		}

		@Property
		void betweenSame(@ForAll("dates") Date sameDate, @ForAll JqwikRandom random) {

			Arbitrary<Date> dates = Dates.datesAsDate().between(sameDate, sameDate);

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

			Arbitrary<Date> dates = Dates.datesAsDate().yearBetween(startYear, endYear);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(dateToCalendar(date).get(Calendar.YEAR)).isGreaterThanOrEqualTo(startYear);
				assertThat(dateToCalendar(date).get(Calendar.YEAR)).isLessThanOrEqualTo(endYear);
				return true;
			});

		}

		@Property
		void yearBetweenSame(@ForAll("years") int year, @ForAll JqwikRandom random) {

			Arbitrary<Date> dates = Dates.datesAsDate().yearBetween(year, year);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(dateToCalendar(date).get(Calendar.YEAR)).isEqualTo(year);
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

			Arbitrary<Date> dates = Dates.datesAsDate().monthBetween(startMonth, endMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date)))
					.isGreaterThanOrEqualTo(Month.of(startMonth));
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isLessThanOrEqualTo(Month.of(endMonth));
				return true;
			});

		}

		@Property
		void monthBetweenMinAfterMax(@ForAll("months") int startMonth, @ForAll("months") int endMonth, @ForAll JqwikRandom random) {

			Assume.that(startMonth > endMonth);

			Arbitrary<Date> dates = Dates.datesAsDate().monthBetween(startMonth, endMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(
					DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isGreaterThanOrEqualTo(Month.of(endMonth)
				);
				assertThat(
					DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isLessThanOrEqualTo(Month.of(startMonth)
				);
				return true;
			});

		}

		@Property
		void monthBetweenSame(@ForAll("months") int month, @ForAll JqwikRandom random) {

			Arbitrary<Date> dates = Dates.datesAsDate().monthBetween(month, month);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isEqualTo(Month.of(month));
				return true;
			});

		}

		@Property
		void monthOnlyMonths(@ForAll @Size(min = 1) Set<Month> months, @ForAll JqwikRandom random) {

			Arbitrary<Date> dates = Dates.datesAsDate().onlyMonths(months.toArray(new Month[]{}));

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarMonthToMonth(dateToCalendar(date))).isIn(months);
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

			Arbitrary<Date> dates = Dates.datesAsDate().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isGreaterThanOrEqualTo(startDayOfMonth);
				assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isLessThanOrEqualTo(endDayOfMonth);
				return true;
			});

		}

		@Property
		void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth, @ForAll JqwikRandom random) {

			Arbitrary<Date> dates = Dates.datesAsDate().dayOfMonthBetween(dayOfMonth, dayOfMonth);

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(dateToCalendar(date).get(Calendar.DAY_OF_MONTH)).isEqualTo(dayOfMonth);
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

			Arbitrary<Date> dates = Dates.datesAsDate().onlyDaysOfWeek(dayOfWeeks.toArray(new DayOfWeek[]{}));

			checkAllGenerated(dates.generator(1000, true), random, date -> {
				assertThat(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date))).isIn(dayOfWeeks);
				return true;
			});
		}

	}

}
