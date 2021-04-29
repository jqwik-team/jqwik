package net.jqwik.time.api.dates.localDate;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

public class ExhaustiveGenerationTests {

	@Example
	void between() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .between(
					 LocalDate.of(42, DECEMBER, 30),
					 LocalDate.of(43, JANUARY, 2)
				 )
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(4); // Cannot know the number of filtered elements in advance
		assertThat(generator).containsExactly(
			LocalDate.of(42, DECEMBER, 30),
			LocalDate.of(42, DECEMBER, 31),
			LocalDate.of(43, JANUARY, 1),
			LocalDate.of(43, JANUARY, 2)
		);
	}

	@Example
	void onlyMonthsWithSameYearAndDayOfMonth() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .yearBetween(1997, 1997)
				 .dayOfMonthBetween(17, 17)
				 .onlyMonths(MARCH, OCTOBER, DECEMBER)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(276); // Cannot know the exact number of filtered elements in advance
		assertThat(generator).containsExactly(
			LocalDate.of(1997, MARCH, 17),
			LocalDate.of(1997, OCTOBER, 17),
			LocalDate.of(1997, DECEMBER, 17)
		);
	}

	@Example
	void onlyDaysOfWeekWithSameYearAndMonth() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .yearBetween(2020, 2020)
				 .monthBetween(12, 12)
				 .onlyDaysOfWeek(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(31); // Cannot know the exact number of filtered elements in advance
		assertThat(generator).containsExactly(
			LocalDate.of(2020, DECEMBER, 3),
			LocalDate.of(2020, DECEMBER, 7),
			LocalDate.of(2020, DECEMBER, 10),
			LocalDate.of(2020, DECEMBER, 14),
			LocalDate.of(2020, DECEMBER, 17),
			LocalDate.of(2020, DECEMBER, 21),
			LocalDate.of(2020, DECEMBER, 24),
			LocalDate.of(2020, DECEMBER, 28),
			LocalDate.of(2020, DECEMBER, 31)
		);
	}

	@Example
	void dayOfMonthBetweenAndBetweenGreater() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .between(
					 LocalDate.of(2010, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .onlyMonths(DECEMBER)
				 .dayOfMonthBetween(27, 28)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(367);
		assertThat(generator).containsExactly(
			LocalDate.of(2010, DECEMBER, 27),
			LocalDate.of(2010, DECEMBER, 28),
			LocalDate.of(2011, DECEMBER, 27),
			LocalDate.of(2011, DECEMBER, 28)
		);
	}

	@Example
	void dayOfMonthBetweenAndBetween() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .between(
					 LocalDate.of(2011, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .onlyMonths(JUNE)
				 .dayOfMonthBetween(21, 22)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(368);
		assertThat(generator).containsExactly(
			LocalDate.of(2011, JUNE, 21),
			LocalDate.of(2011, JUNE, 22),
			LocalDate.of(2012, JUNE, 21),
			LocalDate.of(2012, JUNE, 22)
		);
	}

	@Example
	void dayOfMonthBetweenAndBetweenLess() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .between(
					 LocalDate.of(2010, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .onlyMonths(FEBRUARY)
				 .dayOfMonthBetween(12, 13)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(367);
		assertThat(generator).containsExactly(
			LocalDate.of(2011, FEBRUARY, 12),
			LocalDate.of(2011, FEBRUARY, 13),
			LocalDate.of(2012, FEBRUARY, 12),
			LocalDate.of(2012, FEBRUARY, 13)
		);
	}

	@Example
	void monthBetweenAndBetweenGreater() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .between(
					 LocalDate.of(2010, MAY, 19),
					 LocalDate.of(2012, SEPTEMBER, 25)
				 )
				 .monthBetween(OCTOBER, NOVEMBER)
				 .dayOfMonthBetween(21, 21)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(397);
		assertThat(generator).containsExactly(
			LocalDate.of(2010, OCTOBER, 21),
			LocalDate.of(2010, NOVEMBER, 21),
			LocalDate.of(2011, OCTOBER, 21),
			LocalDate.of(2011, NOVEMBER, 21)
		);
	}

	@Example
	void monthBetweenAndBetween() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .between(
					 LocalDate.of(2011, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .monthBetween(JUNE, JULY)
				 .dayOfMonthBetween(21, 21)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(397);
		assertThat(generator).containsExactly(
			LocalDate.of(2011, JUNE, 21),
			LocalDate.of(2011, JULY, 21),
			LocalDate.of(2012, JUNE, 21),
			LocalDate.of(2012, JULY, 21)
		);
	}

	@Example
	void monthBetweenAndBetweenLess() {
		Optional<ExhaustiveGenerator<LocalDate>> optionalGenerator =
			Dates.dates()
				 .between(
					 LocalDate.of(2010, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .monthBetween(JANUARY, FEBRUARY)
				 .dayOfMonthBetween(20, 20)
				 .exhaustive();
		assertThat(optionalGenerator).isPresent();

		ExhaustiveGenerator<LocalDate> generator = optionalGenerator.get();
		assertThat(generator.maxCount()).isEqualTo(397);
		assertThat(generator).containsExactly(
			LocalDate.of(2011, JANUARY, 20),
			LocalDate.of(2011, FEBRUARY, 20),
			LocalDate.of(2012, JANUARY, 20),
			LocalDate.of(2012, FEBRUARY, 20)
		);
	}

}
