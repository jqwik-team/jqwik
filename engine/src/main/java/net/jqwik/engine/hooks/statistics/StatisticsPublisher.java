package net.jqwik.engine.hooks.statistics;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

public class StatisticsPublisher {
	private final StatisticsReportFormat statisticsReportFormat;
	private final List<StatisticsEntry> entries;
	private final int countCollects;
	private final String label;

	public StatisticsPublisher(
		StatisticsCollectorImpl statisticsCollector,
		StatisticsReportFormat statisticsReportFormat
	) {
		this(
			statisticsReportFormat,
			statisticsCollector.statisticsEntries(),
			statisticsCollector.countAllCollects(),
			statisticsCollector.label()
		);
	}

	@SuppressWarnings("unchecked")
	private StatisticsPublisher(
		StatisticsReportFormat statisticsReportFormat,
		List<? extends StatisticsEntry> entries,
		int countCollects,
		String label
	) {
		this.statisticsReportFormat = statisticsReportFormat;
		this.entries = (List<StatisticsEntry>) entries;
		this.countCollects = countCollects;
		this.label = label;
	}

	public void publish(Reporter reporter, String propertyName) {
		String report = createReport();
		String reportEntryKey = createReportEntryKey(propertyName);
		Tuple.Tuple2<String, String> reportEntry = Tuple.of(reportEntryKey, report);
		if (report.isEmpty()) {
			return;
		}
		reporter.publishValue(reportEntry.get1(), reportEntry.get2());
	}

	private String createReport() {
		List<String> reportLines = statisticsReportFormat.formatReport(entries);
		StringBuilder report = new StringBuilder();
		for (String reportLine : reportLines) {
			report.append(formatReportLine(reportLine));
		}
		return report.toString();
	}

	private String formatReportLine(final String line) {
		return String.format("%n    %s", line);
	}

	private String createReportEntryKey(String propertyName) {
		return String.format("[%s] (%d) %s", propertyName, countCollects, label);
	}

}
