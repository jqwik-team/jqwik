package net.jqwik.api.statistics;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.hooks.statistics.*;

class HistogramTests {

	@Example
	void plainIntegerValues() {
		Histogram histogram = new Histogram();

		List<StatisticsEntry> entries = Arrays.asList(
			new StatisticsEntryImpl(Arrays.asList(30), Integer.toString(30), 1, 1.0),
			new StatisticsEntryImpl(Arrays.asList(1), Integer.toString(1), 5, 5.0),
			new StatisticsEntryImpl(Arrays.asList(2), Integer.toString(2), 10, 10.0)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).containsExactly(
			"   # | label | count | ",
			"-----|-------|-------|---------------------------------------------------------------------------------",
			"   0 |     1 |     5 | ■■■■■",
			"   1 |     2 |    10 | ■■■■■■■■■■",
			"   2 |    30 |     1 | ■"
		);
	}

	@Example
	void complainsWithEmptyValueList() {
		Histogram histogram = new Histogram();

		List<StatisticsEntry> entries = Arrays.asList(
			new StatisticsEntryImpl(Arrays.asList(), "empty", 1, 1.0),
			new StatisticsEntryImpl(Arrays.asList(1), Integer.toString(1), 5, 5.0)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).startsWith(
			"Cannot draw histogram: " + "java.lang.ArrayIndexOutOfBoundsException: 0"
		);
	}

}