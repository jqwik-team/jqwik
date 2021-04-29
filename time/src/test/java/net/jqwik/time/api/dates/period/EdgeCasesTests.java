package net.jqwik.time.api.dates.period;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class EdgeCasesTests {

	@Example
	void defaultEdgeCases() {
		PeriodArbitrary periods = Dates.periods();
		Set<Period> edgeCases = collectEdgeCaseValues(periods.edgeCases());
		assertThat(edgeCases).containsExactlyInAnyOrder(
			Period.of(-1000, 0, 0),
			Period.of(0, 0, 0),
			Period.of(1000, 0, 0)
		);
	}

	@Example
	void betweenEdgeCases() {
		PeriodArbitrary periods = Dates.periods().between(
			Period.ofDays(15), Period.ofMonths(3)
		);

		Set<Period> edgeCases = collectEdgeCaseValues(periods.edgeCases());
		assertThat(edgeCases).containsExactlyInAnyOrder(
			Period.of(0, 0, 15),
			Period.of(0, 3, 0)
		);
	}

}
