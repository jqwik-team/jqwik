package net.jqwik.engine.statistics;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.engine.hooks.statistics.*;

import static org.assertj.core.api.Assertions.*;

@Label("Statistics Reporting")
class StatisticsReportingTests {

	private final StatisticsCollectorImpl collector = new StatisticsCollectorImpl("a label");

	@Group
	class Publishing implements Reporter {

		private String publishedKey;
		private String publishedReport;

		@Override
		public void publishValue(String key, String value) {
			this.publishedKey = key;
			this.publishedReport = value;
		}

		@Override
		public void publishReport(String key, Object object) {
		}

		@Override
		public void publishReports(final String key, final Map<String, Object> objects) {
		}

		@Override
		public void publishValueOnFailure(String key, String value) {
		}

		@Example
		void published_key_is_created_from_label_fullCount_and_propertyName() {
			collector.collect(1);
			collector.collect(2);
			collector.collect(3);
			StatisticsPublisher reportGenerator = new StatisticsPublisher(collector, new StandardStatisticsReportFormat());
			reportGenerator.publish(this, "myProperty");
			assertThat(publishedKey).isEqualTo("[myProperty] (3) a label");
		}

		@Example
		void each_published_report_line_is_indented_by_4_spaces() {
			collector.collect(1);
			collector.collect(2);
			StatisticsReportFormat format = entries -> Arrays.asList("line1", "line2");
			StatisticsPublisher reportGenerator = new StatisticsPublisher(collector, format);
			reportGenerator.publish(this, "myProperty");
			assertThat(publishedReport).isEqualTo(String.format("%n    line1%n    line2"));
		}

		@Example
		void empty_report_is_not_being_published() {
			StatisticsReportFormat format = entries -> Collections.emptyList();
			StatisticsPublisher reportGenerator = new StatisticsPublisher(collector, format);
			reportGenerator.publish(this, "myProperty");
			assertThat(publishedKey).isNull();
			assertThat(publishedReport).isNull();
		}
	}

	@Group
	class StandardFormat {

		private final StatisticsReportFormat standardFormat = new StandardStatisticsReportFormat();

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

			List<StatisticsEntry> entries = getStatisticsEntries();
			List<String> stats = standardFormat.formatReport(entries);

			assertThat(stats).containsExactly(
				"four  (4) : 40 %",
				"three (3) : 30 %",
				"two   (2) : 20 %",
				"one   (1) : 10 %"
			);
		}

		@SuppressWarnings("unchecked")
		private List<StatisticsEntry> getStatisticsEntries() {
			return (List<StatisticsEntry>) (List<? extends StatisticsEntry>) collector.statisticsEntries();
		}

		@Example
		void null_keys_are_also_reported() {
			collector.collect("aKey");
			collector.collect((Object[]) null);
			collector.collect("aKey");
			collector.collect((Object[]) null);

			List<StatisticsEntry> entries = getStatisticsEntries();
			List<String> stats = standardFormat.formatReport(entries);

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

			List<StatisticsEntry> entries = getStatisticsEntries();
			List<String> stats = standardFormat.formatReport(entries);

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

			List<StatisticsEntry> entries = getStatisticsEntries();
			List<String> stats = standardFormat.formatReport(entries);

			assertThat(stats).containsExactlyInAnyOrder(
				"199 (199) : 99.50 %",
				"1   (  1) :  0.50 %"
			);
		}
	}
}
