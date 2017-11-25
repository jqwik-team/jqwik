package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;

import static java.util.Arrays.asList;

class StatisticsCollectionTests {

	@SuppressWarnings({"SuspiciousMethodCalls", "ArraysAsListWithZeroOrOneArgument"})
	@Example
	void countSingleValues() {
		StatisticsCollector collector = new StatisticsCollector();

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
		StatisticsCollector collector = new StatisticsCollector();

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
		StatisticsCollector collector = new StatisticsCollector();

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

		ReportEntry entry = collector.createReportEntry();

		List<String> stats = Arrays
				.stream(entry.getKeyValuePairs().get(StatisticsCollector.KEY_STATISTICS).split(System.getProperty("line.separator"))) //
				.map(String::trim) //
				.filter(s -> !s.isEmpty()) //
				.collect(Collectors.toList());

		Assertions.assertThat(stats).containsExactly( //
				"four  : 40 %", //
				"three : 30 %", //
				"two   : 20 %", //
				"one   : 10 %" //
		);
	}

	@Example
	void reportDoubleValues() {
		StatisticsCollector collector = new StatisticsCollector();

		collector.collect("two", 2);
		collector.collect("three", 3);
		collector.collect("three", 2);
		collector.collect("two", 3);

		ReportEntry entry = collector.createReportEntry();

		List<String> stats = Arrays
				.stream(entry.getKeyValuePairs().get(StatisticsCollector.KEY_STATISTICS).split(System.getProperty("line.separator"))) //
				.map(String::trim) //
				.filter(s -> !s.isEmpty()) //
				.collect(Collectors.toList());

		Assertions.assertThat(stats).containsExactlyInAnyOrder( //
				"two 2   : 25 %", //
				"three 2 : 25 %", //
				"two 3   : 25 %", //
				"three 3 : 25 %" //
		);
	}

}
