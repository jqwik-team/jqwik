package net.jqwik.engine.hooks.statistics;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
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

	private StatisticsPublisher(
		StatisticsReportFormat statisticsReportFormat,
		List<? extends StatisticsEntry> entries,
		int countCollects,
		String label
	) {
		this.statisticsReportFormat = statisticsReportFormat;
		//noinspection unchecked
		this.entries = (List<StatisticsEntry>) entries;
		this.countCollects = countCollects;
		this.label = label;
	}

	public void publish(Reporter reporter, String propertyName) {
		String report = createReport();
		String reportEntryKey = createReportEntryKey(propertyName);
		Tuple.Tuple2<String, String> reportEntry = Tuple.of(reportEntryKey, report);
		reporter.publish(reportEntry.get1(), reportEntry.get2());
	}

	private String createReport() {
		List<String> reportLines = statisticsReportFormat.formatReport(entries);
		StringBuilder report = new StringBuilder();
		for (String reportLine : reportLines) {
			report.append(String.format("%n    "));
			report.append(reportLine);
		}
		return report.toString();
	}

	private String createReportEntryKey(String propertyName) {
		return String.format("[%s] (%d) %s", propertyName, countCollects, label);
	}

}
