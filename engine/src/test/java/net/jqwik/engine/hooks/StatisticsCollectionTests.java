package net.jqwik.engine.hooks;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.data.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.hooks.statistics.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.data.Percentage.*;

@Group
class StatisticsCollectionTests {

	@Group
	class Counting {
		@SuppressWarnings({"SuspiciousMethodCalls", "ArraysAsListWithZeroOrOneArgument"})
		@Example
		void countSingleValues() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

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
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

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
	}

	@Group
	class Reporting {
		@Example
		void reportCollectedPercentagesInDecreasingOrder() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

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
		void nullKeysAreAlsoReported() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("aKey");
			collector.collect(null);
			collector.collect("aKey");
			collector.collect(null);

			Tuple2<String, String> entry = collector.createReportEntry("a property");

			List<String> stats = parseStatistics(entry);
			assertThat(stats).containsExactly(
				"aKey (2) : 50 %",
				"null (2) : 50 %"
			);
		}

		@Example
		void reportDoubleValues() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

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

		private List<String> parseStatistics(Tuple2<String, String> entry) {
			return parseStatistics(entry.get2());
		}

		private List<String> parseStatistics(String reportString) {
			return Arrays.stream(reportString.split(System.getProperty("line.separator")))
						 .map(String::trim)
						 .filter(s -> !s.isEmpty())
						 .collect(Collectors.toList());
		}
	}

	@Group
	class Percentages {

		@Example
		void exactPercentages() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

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

			assertThat(collector.percentage("four")).isEqualTo(40.0);
			assertThat(collector.percentage("three")).isEqualTo(30.0);
			assertThat(collector.percentage("two")).isEqualTo(20.0);
			assertThat(collector.percentage("one")).isEqualTo(10.0);
		}

		@Example
		void unseenValuesHaveZeroPercentage() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("two");
			collector.collect("one");

			assertThat(collector.percentage("zero")).isEqualTo(0.0);
			assertThat(collector.percentage(null)).isEqualTo(0.0);
		}

		@Example
		void nullValueIsAlsoCounted() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect(null);

			assertThat(collector.percentage("one")).isEqualTo(50.0);
			assertThat(collector.percentage(null)).isEqualTo(50.0);
		}

		@Example
		void circaPercentages() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect("two");
			collector.collect("three");

			assertThat(collector.percentage("one")).isCloseTo(33.3, withPercentage(1));
			assertThat(collector.percentage("two")).isCloseTo(33.3, withPercentage(1));
			assertThat(collector.percentage("three")).isCloseTo(33.3, withPercentage(1));
		}

		@Example
		void percentagesAreRecalculated() {
			StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

			collector.collect("one");
			collector.collect("two");

			assertThat(collector.percentage("one")).isEqualTo(50.0);
			assertThat(collector.percentage("two")).isEqualTo(50.0);

			collector.collect("three");
			collector.collect("three");

			assertThat(collector.percentage("one")).isEqualTo(25.0);
			assertThat(collector.percentage("two")).isEqualTo(25.0);
			assertThat(collector.percentage("three")).isEqualTo(50.0);
		}

	}

}
