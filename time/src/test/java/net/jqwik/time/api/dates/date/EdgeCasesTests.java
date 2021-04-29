package net.jqwik.time.api.dates.date;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;
import static net.jqwik.time.api.testingSupport.ForDate.*;

public class EdgeCasesTests {

	@Example
	void all() {
		DateArbitrary dates = Dates.datesAsDate();
		Set<Date> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(3);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			getDate(1900, Calendar.JANUARY, 1),
			getDate(1904, Calendar.FEBRUARY, 29),
			getDate(2500, Calendar.DECEMBER, 31)
		);
	}

	@Example
	void between() {
		DateArbitrary dates =
			Dates.datesAsDate()
				 .between(getDate(100, Calendar.MARCH, 24), getDate(200, Calendar.NOVEMBER, 10));
		Set<Date> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(3);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			getDate(100, Calendar.MARCH, 24),
			getDate(104, Calendar.FEBRUARY, 29),
			getDate(200, Calendar.NOVEMBER, 10)
		);
	}

	@Example
	void betweenMonth() {
		DateArbitrary dates =
			Dates.datesAsDate()
				 .yearBetween(400, 402)
				 .monthBetween(3, 11);
		Set<Date> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			getDate(400, Calendar.MARCH, 1),
			getDate(402, Calendar.NOVEMBER, 30)
		);
	}

}
