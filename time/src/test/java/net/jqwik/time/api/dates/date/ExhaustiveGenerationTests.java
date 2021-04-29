package net.jqwik.time.api.dates.date;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.time.api.testingSupport.ForDate.*;

public class ExhaustiveGenerationTests {

	@Example
	void between() {
		Optional<ExhaustiveGenerator<Date>> optionalGenerator =
			Dates.datesAsDate()
				 .between(
					 getDate(42, Calendar.DECEMBER, 30),
					 getDate(43, Calendar.JANUARY, 2)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Date> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(
			getDate(42, Calendar.DECEMBER, 30),
			getDate(42, Calendar.DECEMBER, 31),
			getDate(43, Calendar.JANUARY, 1),
			getDate(43, Calendar.JANUARY, 2)
		);
	}

	@Example
	void onlyMonthsWithSameYearAndDayOfMonth() {
		Optional<ExhaustiveGenerator<Date>> optionalGenerator =
			Dates.datesAsDate()
				 .yearBetween(1997, 1997)
				 .dayOfMonthBetween(17, 17)
				 .onlyMonths(MARCH, OCTOBER, DECEMBER)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Date> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(276); // Cannot know the exact number of filtered elements in advance
		assertThat(generator).containsExactly(
			getDate(1997, Calendar.MARCH, 17),
			getDate(1997, Calendar.OCTOBER, 17),
			getDate(1997, Calendar.DECEMBER, 17)
		);
	}

	@Example
	void onlyDaysOfWeekWithSameYearAndMonth() {
		Optional<ExhaustiveGenerator<Date>> optionalGenerator =
			Dates.datesAsDate()
				 .yearBetween(2020, 2020)
				 .monthBetween(12, 12)
				 .onlyDaysOfWeek(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<Date> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(31); // Cannot know the exact number of filtered elements in advance
		assertThat(generator).containsExactly(
			getDate(2020, Calendar.DECEMBER, 3),
			getDate(2020, Calendar.DECEMBER, 7),
			getDate(2020, Calendar.DECEMBER, 10),
			getDate(2020, Calendar.DECEMBER, 14),
			getDate(2020, Calendar.DECEMBER, 17),
			getDate(2020, Calendar.DECEMBER, 21),
			getDate(2020, Calendar.DECEMBER, 24),
			getDate(2020, Calendar.DECEMBER, 28),
			getDate(2020, Calendar.DECEMBER, 31)
		);
	}

}
