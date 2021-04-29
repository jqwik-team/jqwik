package net.jqwik.time.api.dates.yearMonth;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class EdgeCasesTests {

	@Example
	void all() {
		YearMonthArbitrary yearMonths = Dates.yearMonths();
		Set<YearMonth> edgeCases = collectEdgeCaseValues(yearMonths.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			YearMonth.of(1900, Month.JANUARY),
			YearMonth.of(2500, Month.DECEMBER)
		);
	}

	@Example
	void between() {

		YearMonthArbitrary yearMonths =
			Dates.yearMonths().between(YearMonth.of(100, Month.MARCH), YearMonth.of(200, Month.OCTOBER));
		Set<YearMonth> edgeCases = collectEdgeCaseValues(yearMonths.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			YearMonth.of(100, Month.MARCH),
			YearMonth.of(200, Month.OCTOBER)
		);

	}

}
