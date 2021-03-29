package net.jqwik.docs.statistics;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.Statistics;
import net.jqwik.api.statistics.*;

import static net.jqwik.api.statistics.StatisticsReport.StatisticsReportMode.*;

@StatisticsReport(format = ReportOnlyCount.class)
class StatisticsReportingExamples {

	@Property
	void reportCountOnly(@ForAll Integer anInt) {
		Statistics.label("count").collect(true);
	}

	@Property
	@StatisticsReport(OFF)
	void dontShowStatistics(@ForAll Integer anInt) {
		String range = anInt < 0 ? "negative" : anInt > 0 ? "positive" : "zero";
		Statistics.collect(range);
	}

	@Property
	@StatisticsReport(format = MyStatisticsFormat.class)
	void statisticsWithHandMadeFormat(@ForAll Integer anInt) {
		String range = anInt < 0 ? "negative" : anInt > 0 ? "positive" : "zero";
		Statistics.collect(range);
	}

	@Property
	@StatisticsReport(label = "count", format = ReportOnlyCount.class)
	@StatisticsReport(label = "rates", format = MyStatisticsFormat.class)
	void differFormatsByLabel(@ForAll Integer anInt) {
		Statistics.label("count").collect(anInt);
		String range = anInt < 0 ? "negative" : anInt > 0 ? "positive" : "zero";
		Statistics.label("rates").collect(range);
	}

	class MyStatisticsFormat implements StatisticsReportFormat {
		@Override
		public List<String> formatReport(List<StatisticsEntry> entries) {
			return entries.stream()
						  .map(e -> String.format("%s: %d", e.name(), e.count()))
						  .collect(Collectors.toList());
		}
	}

	@Group
	class NestedContainer {
		@Property
		void reportCountOnlyInGroup(@ForAll Integer anInt) {
			Statistics.label("count").collect(true);
		}
	}

}

class ReportOnlyCount implements StatisticsReportFormat {

	@Override
	public List<String> formatReport(List<StatisticsEntry> entries) {
		int count = entries.stream().mapToInt(StatisticsEntry::count).sum();
		return Collections.singletonList(String.format("%s entries were collected.", count));
	}
}