package net.jqwik.time.api.dates.calendar;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.time.api.testingSupport.ForCalendar.*;

public class ExhaustiveGenerationTests {

	@Example
	void between() {
		Optional<ExhaustiveGenerator<Calendar>> optionalGenerator =
			Dates.datesAsCalendar()
				 .between(
					 getCalendar(42, Calendar.DECEMBER, 30),
					 getCalendar(43, Calendar.JANUARY, 2)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Calendar> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(
			getCalendar(42, Calendar.DECEMBER, 30),
			getCalendar(42, Calendar.DECEMBER, 31),
			getCalendar(43, Calendar.JANUARY, 1),
			getCalendar(43, Calendar.JANUARY, 2)
		);
	}

	@Example
	void onlyMonthsWithSameYearAndDayOfMonth() {
		Optional<ExhaustiveGenerator<Calendar>> optionalGenerator =
			Dates.datesAsCalendar()
				 .yearBetween(1997, 1997)
				 .dayOfMonthBetween(17, 17)
				 .onlyMonths(Month.MARCH, Month.OCTOBER, Month.DECEMBER)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Calendar> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(276); // Cannot know the exact number of filtered elements in advance
		assertThat(generator).containsExactly(
			getCalendar(1997, Calendar.MARCH, 17),
			getCalendar(1997, Calendar.OCTOBER, 17),
			getCalendar(1997, Calendar.DECEMBER, 17)
		);
	}

	@Example
	void onlyDaysOfWeekWithSameYearAndMonth() {
		Optional<ExhaustiveGenerator<Calendar>> optionalGenerator =
			Dates.datesAsCalendar()
				 .yearBetween(2020, 2020)
				 .monthBetween(12, 12)
				 .onlyDaysOfWeek(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Calendar> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(31); // Cannot know the exact number of filtered elements in advance
		assertThat(generator).containsExactly(
			getCalendar(2020, Calendar.DECEMBER, 3),
			getCalendar(2020, Calendar.DECEMBER, 7),
			getCalendar(2020, Calendar.DECEMBER, 10),
			getCalendar(2020, Calendar.DECEMBER, 14),
			getCalendar(2020, Calendar.DECEMBER, 17),
			getCalendar(2020, Calendar.DECEMBER, 21),
			getCalendar(2020, Calendar.DECEMBER, 24),
			getCalendar(2020, Calendar.DECEMBER, 28),
			getCalendar(2020, Calendar.DECEMBER, 31)
		);
	}

}
