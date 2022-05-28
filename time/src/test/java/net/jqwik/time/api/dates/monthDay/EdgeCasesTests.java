package net.jqwik.time.api.dates.monthDay;

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
		MonthDayArbitrary monthDays = Dates.monthDays();
		Set<MonthDay> edgeCases = collectEdgeCaseValues(monthDays.edgeCases());
		assertThat(edgeCases).hasSize(3);
		// February 29 is added via edgeCases.add in net.jqwik.time.internal.properties.arbitraries.DefaultLocalDateArbitrary.arbitrary
		// And net.jqwik.engine.properties.arbitraries.GenericEdgeCasesConfiguration.configure adds all the explicitly
		// added edge cases to the end of the list. So February 29 is generated the last.
		assertThat(edgeCases).containsExactly(
			MonthDay.of(Month.JANUARY, 1),
			MonthDay.of(Month.DECEMBER, 31),
			MonthDay.of(Month.FEBRUARY, 29)
		);
	}

	@Example
	void between() {
		MonthDayArbitrary monthDays =
			Dates.monthDays().between(MonthDay.of(Month.FEBRUARY, 25), MonthDay.of(Month.APRIL, 10));
		Set<MonthDay> edgeCases = collectEdgeCaseValues(monthDays.edgeCases());
		assertThat(edgeCases).hasSize(3);
		assertThat(edgeCases).containsExactlyInAnyOrder(
			MonthDay.of(Month.FEBRUARY, 25),
			MonthDay.of(Month.FEBRUARY, 29),
			MonthDay.of(Month.APRIL, 10)
		);
	}

}
