package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;

import static java.util.Arrays.*;

class StatisticsCollectionTests {

	@SuppressWarnings({"SuspiciousMethodCalls", "ArraysAsListWithZeroOrOneArgument"})
	@Example
	void countSingleValues() {
		StatisticsCollectorImpl collector = new StatisticsCollectorImpl(StatisticsCollectorImpl.DEFAULT_LABEL);

		collector.collect("two");
		collector.collect("three");
		collector.collect("two");
		collector.collect("one");
		collector.collect("three");
		collector.collect("three");

		Map<List<Object>, Integer> counts = collector.getCounts();
		Assertions.assertThat(counts.get(asList("one"))).isEqualTo(1);
		Assertions.assertThat(counts.get(asList("two"))).isEqualTo(2);
		Assertions.assertThat(counts.get(asList("three"))).isEqualTo(3);
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	@Example
	void countDoubleValues() {
		StatisticsCollectorImpl collector = new StatisticsCollectorImpl(StatisticsCollectorImpl.DEFAULT_LABEL);

		collector.collect("two", 2);
		collector.collect("three", 3);
		collector.collect("two", 2);
		collector.collect("one", 1);
		collector.collect("three", 3);
		collector.collect("three", 3);

		Map<List<Object>, Integer> counts = collector.getCounts();
		Assertions.assertThat(counts.get(asList("one", 1))).isEqualTo(1);
		Assertions.assertThat(counts.get(asList("two", 2))).isEqualTo(2);
		Assertions.assertThat(counts.get(asList("three", 3))).isEqualTo(3);
	}

	@Example
	void reportCollectedPercentagesInDecreasingOrder() {
		StatisticsCollectorImpl collector = new StatisticsCollectorImpl(StatisticsCollectorImpl.DEFAULT_LABEL);

		collector.collect("two");
		collector.collect("three");
		collector.collect("two");
		collector.collect("four");
		collector.collect("four");
		collector.collect("one");
		collector.collect("three");
		collector.collect("four");
		collector.collect("four");
		collector.collect("three");

		ReportEntry entry = collector.createReportEntry("a property");

		List<String> stats = parseStatistics(entry);
		Assertions.assertThat(stats).containsExactly(
			"four  (4) : 40 %",
			"three (3) : 30 %",
			"two   (2) : 20 %",
			"one   (1) : 10 %"
		);
	}

	@Example
	void reportWithDifferentLabel() {
		StatisticsCollectorImpl collector = new StatisticsCollectorImpl("label");

		collector.collect("two");
		collector.collect("two");
		collector.collect("two");
		collector.collect("one");

		ReportEntry entry = collector.createReportEntry("a property");

		List<String> stats = parseStatistics(entry, "label");
		Assertions.assertThat(stats).containsExactly(
			"two (3) : 75 %",
			"one (1) : 25 %"
		);
	}

	@Example
	void nullKeysAreNotReported() {
		StatisticsCollectorImpl collector = new StatisticsCollectorImpl(StatisticsCollectorImpl.DEFAULT_LABEL);

		collector.collect("aKey");
		collector.collect((Object) null);
		collector.collect("aKey");
		collector.collect((Object) null);

		ReportEntry entry = collector.createReportEntry("a property");

		List<String> stats = parseStatistics(entry);
		Assertions.assertThat(stats).containsExactly(
			"aKey (2) : 50 %"
		);
	}

	private List<String> parseStatistics(ReportEntry entry) {
		String label = StatisticsCollectorImpl.DEFAULT_LABEL;
		return parseStatistics(entry, label);
	}

	private List<String> parseStatistics(ReportEntry entry, String label) {
		return Arrays.stream(getValue(entry, label)
								  .split(System.getProperty("line.separator")))
					 .map(String::trim)
					 .filter(s -> !s.isEmpty())
					 .collect(Collectors.toList());
	}

	private String getValue(ReportEntry entry, String label) {
		return entry.getKeyValuePairs().entrySet()
					.stream()
					.filter(e -> e.getKey().contains(label))
					.map(Map.Entry::getValue)
					.findFirst().orElse(null);
	}

	@Example
	void reportDoubleValues() {
		StatisticsCollectorImpl collector = new StatisticsCollectorImpl(StatisticsCollectorImpl.DEFAULT_LABEL);

		collector.collect("two", 2);
		collector.collect("three", 3);
		collector.collect("three", 2);
		collector.collect("two", 3);

		ReportEntry entry = collector.createReportEntry("a property");

		List<String> stats = parseStatistics(entry);
		Assertions.assertThat(stats).containsExactlyInAnyOrder(
			"two 2   (1) : 25 %",
			"three 2 (1) : 25 %",
			"two 3   (1) : 25 %",
			"three 3 (1) : 25 %"
		);
	}

}
