package net.jqwik.time.api.dates.year;

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
		YearArbitrary years = Dates.years();
		Set<Year> edgeCases = collectEdgeCaseValues(years.edgeCases());
		assertThat(edgeCases).hasSize(4);
		assertThat(edgeCases)
			.containsExactlyInAnyOrder(Year.of(1900), Year.of(1901), Year.of(2499), Year.of(2500));
	}

	@Example
	void between() {
		YearArbitrary years = Dates.years().between(100, 200);
		Set<Year> edgeCases = collectEdgeCaseValues(years.edgeCases());
		assertThat(edgeCases).hasSize(4);
		assertThat(edgeCases).containsExactlyInAnyOrder(Year.of(100), Year.of(101), Year.of(199), Year.of(200));
	}

}
