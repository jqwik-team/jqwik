package net.jqwik.engine.hooks;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.hooks.statistics.*;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("ConfusingArgumentToVarargsMethod")
@Label("Statistics Reporting")
class StatisticsReportingTests {

	private final StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

	@Example
	void entryKey_is_created_from_label_fullCount_and_propertyName() {
		collector.collect(1);
		collector.collect(2);
		collector.collect(3);

		StatisticsReportGenerator reportGenerator = new StatisticsReportGenerator(collector);
		String reportKey = reportGenerator.createReportEntryKey("myProperty");

		assertThat(reportKey).isEqualTo("[myProperty] (3) a label");
	}

	@Example
	void report_collected_percentages_in_decreasing_order() {
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

		String report = new StatisticsReportGenerator(collector).createReport();
		List<String> stats = parseStatistics(report);

		assertThat(stats).containsExactly(
			"four  (4) : 40 %",
			"three (3) : 30 %",
			"two   (2) : 20 %",
			"one   (1) : 10 %"
		);
	}

	@Example
	void null_keys_are_also_reported() {
		collector.collect("aKey");
		collector.collect(null);
		collector.collect("aKey");
		collector.collect(null);

		String report = new StatisticsReportGenerator(collector).createReport();
		List<String> stats = parseStatistics(report);

		assertThat(stats).containsExactly(
			"aKey (2) : 50 %",
			"null (2) : 50 %"
		);
	}

	@Example
	void report_pairs_of_values() {
		collector.collect("two", 2);
		collector.collect("three", 3);
		collector.collect("three", 2);
		collector.collect("two", 3);

		String report = new StatisticsReportGenerator(collector).createReport();
		List<String> stats = parseStatistics(report);

		assertThat(stats).containsExactlyInAnyOrder(
			"two 2   (1) : 25 %",
			"three 2 (1) : 25 %",
			"two 3   (1) : 25 %",
			"three 3 (1) : 25 %"
		);
	}

	@Example
	void report_percentages_with_decimals_if_smaller_than_1() {
		for (int i = 0; i < 199; i++) {
			collector.collect("199");
		}
		collector.collect("1");

		String report = new StatisticsReportGenerator(collector).createReport();
		List<String> stats = parseStatistics(report);

		assertThat(stats).containsExactlyInAnyOrder(
			"199 (199) : 99,50 %",
			"1   (  1) :  0,50 %"
		);
	}

	private List<String> parseStatistics(String reportString) {
		return Arrays.stream(reportString.split(System.getProperty("line.separator")))
					 .map(String::trim)
					 .filter(s -> !s.isEmpty())
					 .collect(Collectors.toList());
	}
}
