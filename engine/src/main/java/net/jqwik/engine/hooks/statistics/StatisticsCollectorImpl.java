package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class StatisticsCollectorImpl implements StatisticsCollector {
	public static final String STORE_NAME = "statistics";

	private final Map<List<Object>, Integer> counts = new HashMap<>();

	private final String label;

	public StatisticsCollectorImpl(String label) {
		this.label = label;
	}

	@Override
	public void collect(Object... values) {
		List<Object> key = Collections.emptyList();
		if (values != null) {
			key = Arrays.stream(values) //
						.filter(Objects::nonNull) //
						.collect(Collectors.toList());
		}

		int count = counts.computeIfAbsent(key, any -> 0);
		counts.put(key, ++count);
	}

	public Map<List<Object>, Integer> getCounts() {
		return counts;
	}

	public Tuple2<String, String> createReportEntry(String propertyName) {
		StringBuilder statistics = new StringBuilder();
		int sum = counts.values().stream().mapToInt(aCount -> aCount).sum();
		List<StatisticsEntry> statisticsEntries =
			counts.entrySet().stream()
				  .sorted(this::compareStatisticsEntries)
				  .filter(entry -> !entry.getKey().equals(Collections.emptyList()))
				  .map(entry -> new StatisticsEntry(displayKey(entry.getKey()), entry.getValue(), entry.getValue() * 100.0 / sum))
				  .collect(Collectors.toList());
		int maxKeyLength = statisticsEntries.stream().mapToInt(entry -> entry.name.length()).max().orElse(0);
		boolean fullNumbersOnly = statisticsEntries.stream().noneMatch(entry -> entry.percentage < 1);

		final int decimals = (int) Math.max(1, Math.round(Math.log10(sum)));
		for (StatisticsEntry statsEntry : statisticsEntries) {
			statistics.append(formatEntry(statsEntry, maxKeyLength, fullNumbersOnly, decimals));
		}

		String keyStatistics = String.format("[%s] (%d) %s", propertyName, sum, label);
		return Tuple.of(keyStatistics, statistics.toString());
	}

	private String formatEntry(StatisticsEntry statsEntry, int maxKeyLength, boolean fullNumbersOnly, int decimals) {
		return String.format(
			"%n    %1$-" + maxKeyLength + "s (%2$" + decimals + "d) : %3$s %%",
			statsEntry.name,
			statsEntry.count,
			displayPercentage(statsEntry.percentage, fullNumbersOnly)
		);
	}

	private int compareStatisticsEntries(Map.Entry<List<Object>, Integer> e1, Map.Entry<List<Object>, Integer> e2) {
		List<Object> k1 = e1.getKey();
		List<Object> k2 = e2.getKey();
		if (k1.size() != k2.size()) {
			return Integer.compare(k1.size(), k2.size());
		}
		return e2.getValue().compareTo(e1.getValue());
	}

	private String displayPercentage(double percentage, boolean fullNumbersOnly) {
		if (fullNumbersOnly)
			return String.format("%2d", Math.round(percentage));
		return String.format("%5.2f", Math.round(percentage * 100.0) / 100.0);
	}

	private String displayKey(List<Object> key) {
		return key.stream().map(Object::toString).collect(Collectors.joining(" "));
	}

	static class StatisticsEntry {
		private final String name;
		private long count;
		private final double percentage;

		StatisticsEntry(String name, long count, double percentage) {
			this.name = name;
			this.count = count;
			this.percentage = percentage;
		}
	}
}
