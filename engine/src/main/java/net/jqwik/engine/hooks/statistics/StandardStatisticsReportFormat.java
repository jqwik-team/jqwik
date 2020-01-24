package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.statistics.*;

public class StandardStatisticsReportFormat implements StatisticsReportFormat {

	public Optional<List<String>> formatReport(List<StatisticsEntry> entries) {
		int maxKeyLength = entries.stream().mapToInt(entry -> entry.name().length()).max().orElse(0);
		boolean fullNumbersOnly = entries.stream().noneMatch(entry -> entry.percentage() < 1);
		int maxCount = entries.stream().mapToInt(StatisticsEntry::count).max().orElse(0);
		int decimals = (int) Math.max(1, Math.floor(Math.log10(maxCount)) + 1);

		List<String> reportLines =
			entries
				.stream()
				.map(entry -> formatEntry(entry, maxKeyLength, fullNumbersOnly, decimals))
				.collect(Collectors.toList());

		return Optional.of(reportLines);
	}

	private String formatEntry(StatisticsEntry statsEntry, int maxKeyLength, boolean fullNumbersOnly, int decimals) {
		return String.format(
			"%1$-" + maxKeyLength + "s (%2$" + decimals + "d) : %3$s %%",
			statsEntry.name(),
			statsEntry.count(),
			displayPercentage(statsEntry.percentage(), fullNumbersOnly)
		);
	}

	private String displayPercentage(double percentage, boolean fullNumbersOnly) {
		if (fullNumbersOnly)
			return String.format("%2d", Math.round(percentage));
		return String.format("%5.2f", Math.round(percentage * 100.0) / 100.0);
	}

}
