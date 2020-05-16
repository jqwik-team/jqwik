package net.jqwik.api.statistics;

import java.util.ArrayList;
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
	void clusterTwoValuesIntoOne() {
		Histogram histogram = new Histogram() {
			@Override
			protected List<Bucket> cluster(final List<StatisticsEntry> entries) {
				List<Bucket> buckets = new ArrayList<>();
				buckets.add(new Bucket("[1..2]"));
				buckets.add(new Bucket("[3..4]"));

				for (int i = 0; i < entries.size(); i++) {
					StatisticsEntry entry = entries.get(i);
					if ((int) entry.values().get(0) < 3) {
						buckets.get(0).addCount(entry.count());
					} else {
						buckets.get(1).addCount(entry.count());
					}
				}

				return buckets;
			}
		};

		List<StatisticsEntry> entries = asList(
			createEntry(asList(1), 500),
			createEntry(asList(2), 100),
			createEntry(asList(3), 200),
			createEntry(asList(4), 10)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).containsExactly(
			"   # |  label | count | ",
			"-----|--------|-------|---------------------------------------------------------------------------------",
			"   0 | [1..2] |   600 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■",
			"   1 | [3..4] |   210 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■"
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
	void uncomparableValuesAreComparedByTheirNameString() {
		Histogram histogram = new Histogram();

		Object uncomparable = new Object() {
			@Override
			public String toString() {
				return "0 uncomparable";
			}
		};
		List<StatisticsEntry> entries = asList(
			createEntry(asList(uncomparable), 1),
			createEntry(asList(1), 5)
		);

		List<String> report = histogram.formatReport(entries);

		Assertions.assertThat(report).containsExactly(
			"   # |          label | count | ",
			"-----|----------------|-------|---------------------------------------------------------------------------------",
			"   0 | 0 uncomparable |     1 | ■",
			"   1 |              1 |     5 | ■■■■■"
		);
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

	@Group
	class RangeHistograms {

		@Example
		void integerRanges() {
			Histogram histogram = new NumberRangeHistogram() {
				@Override
				protected int buckets() {
					return 5;
				}

				@Override
				protected int maxDrawRange() {
					return 10;
				}
			};

			List<StatisticsEntry> entries = asList(
				createEntry(asList(1), 10),
				createEntry(asList(2), 10),
				createEntry(asList(3), 10),
				createEntry(asList(4), 10),
				createEntry(asList(5), 10),
				createEntry(asList(6), 10),
				createEntry(asList(7), 10),
				createEntry(asList(8), 10),
				createEntry(asList(9), 10),
				createEntry(asList(10), 10)
			);

			List<String> report = histogram.formatReport(entries);

			Assertions.assertThat(report).containsExactly(
				"   # |   label | count | ",
				"-----|---------|-------|-----------",
				"   0 |  [1..3[ |    20 | ■■■■■■■■■■",
				"   1 |  [3..5[ |    20 | ■■■■■■■■■■",
				"   2 |  [5..7[ |    20 | ■■■■■■■■■■",
				"   3 |  [7..9[ |    20 | ■■■■■■■■■■",
				"   4 | [9..10] |    20 | ■■■■■■■■■■"
			);
		}

		@Example
		void doubleRanges() {
			Histogram histogram = new NumberRangeHistogram() {
				@Override
				protected int buckets() {
					return 5;
				}

				@Override
				protected int maxDrawRange() {
					return 10;
				}
			};

			List<StatisticsEntry> entries = asList(
				createEntry(asList(1.0), 10),
				createEntry(asList(1.5), 10),
				createEntry(asList(2.0), 10),
				createEntry(asList(3.0), 10),
				createEntry(asList(3.5), 10),
				createEntry(asList(4.0), 10),
				createEntry(asList(5.0), 10),
				createEntry(asList(5.5), 10),
				createEntry(asList(6.0), 10),
				createEntry(asList(7.0), 10),
				createEntry(asList(7.5), 10),
				createEntry(asList(8.0), 10),
				createEntry(asList(9.0), 10),
				createEntry(asList(9.5), 10),
				createEntry(asList(10.0), 10)
			);

			List<String> report = histogram.formatReport(entries);

			Assertions.assertThat(report).containsExactly(
				"   # |   label | count | ",
				"-----|---------|-------|-----------",
				"   0 |  [1..3[ |    30 | ■■■■■■■■■■",
				"   1 |  [3..5[ |    30 | ■■■■■■■■■■",
				"   2 |  [5..7[ |    30 | ■■■■■■■■■■",
				"   3 |  [7..9[ |    30 | ■■■■■■■■■■",
				"   4 | [9..10] |    30 | ■■■■■■■■■■"
			);
		}

	}

	private String displayKey(List<Object> key) {
		return key.stream().map(Objects::toString).collect(Collectors.joining(" "));
	}

}