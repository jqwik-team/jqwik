package net.jqwik.time.api.dates.calendar;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;
import static net.jqwik.time.api.testingSupport.ForCalendar.*;

public class EdgeCasesTests {

	@Example
	void all() {
		CalendarArbitrary dates = Dates.datesAsCalendar();
		Set<Calendar> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(3);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			getCalendar(1900, Calendar.JANUARY, 1),
			getCalendar(1904, Calendar.FEBRUARY, 29),
			getCalendar(2500, Calendar.DECEMBER, 31)
		);
	}

	@Example
	void between() {
		CalendarArbitrary dates =
			Dates.datesAsCalendar()
				 .between(getCalendar(100, Calendar.MARCH, 24), getCalendar(200, Calendar.NOVEMBER, 10));
		Set<Calendar> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(3);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			getCalendar(100, Calendar.MARCH, 24),
			getCalendar(104, Calendar.FEBRUARY, 29),
			getCalendar(200, Calendar.NOVEMBER, 10)
		);
	}

	@Example
	void betweenMonth() {
		CalendarArbitrary dates =
			Dates.datesAsCalendar()
				 .yearBetween(400, 402)
				 .monthBetween(3, 11);
		Set<Calendar> edgeCases = collectEdgeCaseValues(dates.edgeCases());
		assertThat(edgeCases).hasSize(2);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			getCalendar(400, Calendar.MARCH, 1),
			getCalendar(402, Calendar.NOVEMBER, 30)
		);
	}

}
