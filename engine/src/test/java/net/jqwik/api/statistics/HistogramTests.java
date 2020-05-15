package net.jqwik.api.statistics;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.hooks.statistics.*;

import static java.util.Arrays.*;

class HistogramTests {

	@Example
	void plainIntegerValues() {
		Histogram histogram = new Histogram();

		List<StatisticsEntry> entries = asList(
			createEntry(asList(30), 1),
			createEntry(asList(1), 5),
			createEntry(asList(2), 10)
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
	void reducedMaxDrawRange() {
		Histogram histogram = new Histogram() {
			@Override
			protected int maxDrawRange() {
				return 20;
			}
		};

		List<StatisticsEntry> entries = asList(
			createEntry(asList(1), 500),
			createEntry(asList(2), 100)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).containsExactly(
			"   # | label | count | ",
			"-----|-------|-------|---------------------",
			"   0 |     1 |   500 | ■■■■■■■■■■■■■■■■■■■■",
			"   1 |     2 |   100 | ■■■■"
		);
	}

	@Example
	void reverseSort() {
		Histogram histogram = new Histogram() {
			@Override
			protected Comparator<? super StatisticsEntry> comparator() {
				return super.comparator().reversed();
			}
		};

		List<StatisticsEntry> entries = asList(
			createEntry(asList(1), 500),
			createEntry(asList(2), 100)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).containsExactly(
			"   # | label | count | ",
			"-----|-------|-------|---------------------------------------------------------------------------------",
			"   0 |     2 |   100 | ■■■■■■■■■■■■■■■■",
			"   1 |     1 |   500 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■"
		);
	}

	@Example
	void changeLabel() {
		Histogram histogram = new Histogram() {
			@Override
			protected String label(final StatisticsEntry entry) {
				return entry.name() + entry.name();
			}
		};

		List<StatisticsEntry> entries = asList(
			createEntry(asList(1), 500),
			createEntry(asList(2), 100)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).containsExactly(
			"   # | label | count | ",
			"-----|-------|-------|---------------------------------------------------------------------------------",
			"   0 |    11 |   500 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■",
			"   1 |    22 |   100 | ■■■■■■■■■■■■■■■■"
		);
	}

	@Example
	void multiValues() {
		Histogram histogram = new Histogram();

		List<StatisticsEntry> entries = asList(
			createEntry(asList(3, "whatever"), 1),
			createEntry(asList(1, "oops"), 5),
			createEntry(asList(2, "two"), 10)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).containsExactly(
			"   # |      label | count | ",
			"-----|------------|-------|---------------------------------------------------------------------------------",
			"   0 |     1 oops |     5 | ■■■■■",
			"   1 |      2 two |    10 | ■■■■■■■■■■",
			"   2 | 3 whatever |     1 | ■"
		);
	}

	private StatisticsEntry createEntry(List<Object> values, int count) {
		return new StatisticsEntryImpl(values, displayKey(values), count, 42.0);
	}

	@Example
	void complainsOnEmptyValueList() {
		Histogram histogram = new Histogram();

		List<StatisticsEntry> entries = asList(
			createEntry(asList(), 1),
			createEntry(asList(1), 5)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report.get(0)).startsWith(
			"Cannot draw histogram: "
		);
	}

	@Example
	void complainsOnUncomparableValues() {
		Histogram histogram = new Histogram();

		List<StatisticsEntry> entries = asList(
			createEntry(asList(new Object()), 1),
			createEntry(asList(1), 5)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report.get(0)).startsWith(
			"Cannot draw histogram: "
		);
	}

	private String displayKey(List<Object> key) {
		return key.stream().map(Objects::toString).collect(Collectors.joining(" "));
	}

}