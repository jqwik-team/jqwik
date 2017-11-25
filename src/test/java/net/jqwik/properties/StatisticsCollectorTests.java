package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import org.assertj.core.api.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;

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

		List<String> stats = Arrays
				.stream(entry.getKeyValuePairs().get(StatisticsCollector.KEY_STATISTICS).split(System.getProperty("line.separator"))) //
				.map(String::trim) //
				.filter(s -> !s.isEmpty()) //
				.collect(Collectors.toList());

		Assertions.assertThat(stats).containsExactlyInAnyOrder( //
				"one   : 10 %", //
				"two   : 20 %", //
				"three : 30 %", //
				"four  : 40 %" //
		);
	}
}
