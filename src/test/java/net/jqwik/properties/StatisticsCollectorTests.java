package net.jqwik.properties;

import net.jqwik.api.*;
import org.assertj.core.api.*;
import org.junit.platform.engine.reporting.*;

import java.util.*;

class StatisticsCollectorTests {

	@Example
	void reportPercentages() {
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

		Map<String, String> counts = entry.getKeyValuePairs();
		Assertions.assertThat(counts.get("one")).isEqualTo("10 %");
		Assertions.assertThat(counts.get("two")).isEqualTo("20 %");
		Assertions.assertThat(counts.get("three")).isEqualTo("30 %");
		Assertions.assertThat(counts.get("four")).isEqualTo("40 %");
	}
}
