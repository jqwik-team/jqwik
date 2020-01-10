package net.jqwik.engine.hooks;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.hooks.statistics.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

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
		assertThat(counts.get(asList("one"))).isEqualTo(1);
		assertThat(counts.get(asList("two"))).isEqualTo(2);
		assertThat(counts.get(asList("three"))).isEqualTo(3);
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
		assertThat(counts.get(asList("one", 1))).isEqualTo(1);
		assertThat(counts.get(asList("two", 2))).isEqualTo(2);
		assertThat(counts.get(asList("three", 3))).isEqualTo(3);
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

		Tuple2<String, String> entry = collector.createReportEntry("a property");

		List<String> stats = parseStatistics(entry);
		assertThat(stats).containsExactly(
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

		Tuple2<String, String> entry = collector.createReportEntry("a property");
		assertThat(entry.get1()).isEqualTo("[a property] (4) label");

		List<String> stats = parseStatistics(entry);
		assertThat(stats).containsExactly(
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

		Tuple2<String, String> entry = collector.createReportEntry("a property");

		List<String> stats = parseStatistics(entry);
		assertThat(stats).containsExactly(
			"aKey (2) : 50 %"
		);
	}

	private List<String> parseStatistics(Tuple2<String, String> entry) {
		return parseStatistics(entry.get2());
	}

	private List<String> parseStatistics(String reportString) {
		return Arrays.stream(reportString.split(System.getProperty("line.separator")))
					 .map(String::trim)
					 .filter(s -> !s.isEmpty())
					 .collect(Collectors.toList());
	}

	@Example
	void reportDoubleValues() {
		StatisticsCollectorImpl collector = new StatisticsCollectorImpl(StatisticsCollectorImpl.DEFAULT_LABEL);

		collector.collect("two", 2);
		collector.collect("three", 3);
		collector.collect("three", 2);
		collector.collect("two", 3);

		Tuple2<String, String> entry = collector.createReportEntry("a property");

		List<String> stats = parseStatistics(entry);
		assertThat(stats).containsExactlyInAnyOrder(
			"two 2   (1) : 25 %",
			"three 2 (1) : 25 %",
			"two 3   (1) : 25 %",
			"three 3 (1) : 25 %"
		);
	}

}
