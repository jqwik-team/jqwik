package net.jqwik.time.api.dates.localDate;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class EdgeCasesTests {

	@Example
	void all() {
		LocalDateArbitrary dates = Dates.dates();
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(3);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(1900, 1, 1),
			LocalDate.of(1904, 2, 29),
			LocalDate.of(2500, 12, 31)
		);
	}

	@Example
	void between() {
		LocalDateArbitrary dates =
			Dates.dates()
				 .between(LocalDate.of(100, MARCH, 24), LocalDate.of(200, NOVEMBER, 10));
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(3);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(100, MARCH, 24),
			LocalDate.of(104, FEBRUARY, 29),
			LocalDate.of(200, NOVEMBER, 10)
		);
	}

	@Example
	void betweenMonth() {
		LocalDateArbitrary dates =
			Dates.dates()
				 .yearBetween(400, 402)
				 .monthBetween(3, 11);
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(400, MARCH, 1),
			LocalDate.of(402, NOVEMBER, 30)
		);
	}

	@Example
	void dayOfMonthBetweenAndBetweenGreater() {
		LocalDateArbitrary dates =
			Dates.dates()
				 .between(
					 LocalDate.of(2010, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .onlyMonths(DECEMBER)
				 .dayOfMonthBetween(27, 28);
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(2010, DECEMBER, 27),
			LocalDate.of(2011, DECEMBER, 28)
		);
	}

	@Example
	void dayOfMonthBetweenAndBetween() {
		LocalDateArbitrary dates =
			Dates.dates()
				 .between(
					 LocalDate.of(2011, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .onlyMonths(JUNE)
				 .dayOfMonthBetween(21, 22);
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(2011, JUNE, 21),
			LocalDate.of(2012, JUNE, 22)
		);
	}

	@Example
	void dayOfMonthBetweenAndBetweenLess() {
		LocalDateArbitrary dates =
			Dates.dates()
				 .between(
					 LocalDate.of(2010, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .onlyMonths(FEBRUARY)
				 .dayOfMonthBetween(12, 13);
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(2011, FEBRUARY, 12),
			LocalDate.of(2012, FEBRUARY, 13)
		);
	}

	@Example
	void monthBetweenAndBetweenGreater() {
		LocalDateArbitrary dates =
			Dates.dates()
				 .between(
					 LocalDate.of(2010, MAY, 19),
					 LocalDate.of(2012, SEPTEMBER, 25)
				 )
				 .monthBetween(OCTOBER, NOVEMBER)
				 .dayOfMonthBetween(21, 21);
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(2010, OCTOBER, 21),
			LocalDate.of(2011, NOVEMBER, 21)
		);
	}

	@Example
	void monthBetweenAndBetween() {
		LocalDateArbitrary dates =
			Dates.dates()
				 .between(
					 LocalDate.of(2011, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .monthBetween(JUNE, JULY)
				 .dayOfMonthBetween(21, 21);
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(2011, JUNE, 21),
			LocalDate.of(2012, JULY, 21)
		);
	}

	@Example
	void monthBetweenAndBetweenLess() {
		LocalDateArbitrary dates =
			Dates.dates()
				 .between(
					 LocalDate.of(2010, MAY, 19),
					 LocalDate.of(2012, NOVEMBER, 25)
				 )
				 .monthBetween(JANUARY, FEBRUARY)
				 .dayOfMonthBetween(20, 20);
		Set<LocalDate> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			LocalDate.of(2011, JANUARY, 20),
			LocalDate.of(2012, FEBRUARY, 20)
		);
	}

}
