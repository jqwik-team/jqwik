package net.jqwik.api.statistics;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.hooks.statistics.*;

class HistogramReportFormatTests {

	@Example
	void plainValues() {
		Histogram histogram = new Histogram();

		List<StatisticsEntry> entries = Arrays.asList(
			new StatisticsEntryImpl(Arrays.asList("three"), "3", 1, 1.0),
			new StatisticsEntryImpl(Arrays.asList("one"), "1", 5, 5.0),
			new StatisticsEntryImpl(Arrays.asList("two"), "2", 10, 10.0)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).containsExactly(
			"   # | label | count | ",
			"-----|-------|-------|---------------------------------------------------------------------------------",
			"   0 |     1 |     5 | ■■■■■",
			"   1 |     2 |    10 | ■■■■■■■■■■",
			"   2 |     3 |     1 | ■"
		);
	}

}
