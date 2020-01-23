package net.jqwik.engine.hooks.statistics;

import java.util.*;

public class StatisticsReportGenerator {
	private final List<StatisticsEntry> entries;
	private final int countCollects;
	private String label;

	public StatisticsReportGenerator(StatisticsCollectorImpl statisticsCollector) {
		this(statisticsCollector.statisticsEntries(), statisticsCollector.countAllCollects(), statisticsCollector.label());
	}

	private StatisticsReportGenerator(List<StatisticsEntry> entries, int countCollects, String label) {
		this.entries = entries;
		this.countCollects = countCollects;
		this.label = label;
	}

	public String createReport() {
		int maxKeyLength = entries.stream().mapToInt(entry -> entry.name.length()).max().orElse(0);
		boolean fullNumbersOnly = entries.stream().noneMatch(entry -> entry.percentage < 1);
		int maxCount = entries.stream().mapToInt(entry -> entry.count).max().orElse(0);
		int decimals = (int) Math.max(1, Math.floor(Math.log10(maxCount)) + 1);

		StringBuilder report = new StringBuilder();
		for (StatisticsEntry statsEntry : entries) {
			report.append(formatEntry(statsEntry, maxKeyLength, fullNumbersOnly, decimals));
		}
		return report.toString();
	}

	public String createReportEntryKey(String propertyName) {
		return String.format("[%s] (%d) %s", propertyName, countCollects, label);
	}

	private String formatEntry(StatisticsEntry statsEntry, int maxKeyLength, boolean fullNumbersOnly, int decimals) {
		return String.format(
			"%n    %1$-" + maxKeyLength + "s (%2$" + decimals + "d) : %3$s %%",
			statsEntry.name,
			statsEntry.count,
			displayPercentage(statsEntry.percentage, fullNumbersOnly)
		);
	}

	private String displayPercentage(double percentage, boolean fullNumbersOnly) {
		if (fullNumbersOnly)
			return String.format("%2d", Math.round(percentage));
		return String.format("%5.2f", Math.round(percentage * 100.0) / 100.0);
	}

}
