package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.Statistics.*;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class StatisticsCollectorImpl implements StatisticsCollector {
	public static final String STORE_NAME = String.format("%s:statistics", StatisticsCollector.class.getName());

	private final Map<List<Object>, Integer> counts = new HashMap<>();

	private final String label;
	private List<StatisticsEntry> statisticsEntries = null;

	public StatisticsCollectorImpl(String label) {
		this.label = label;
	}

	@Override
	public void collect(Object... values) {
		ensureAtLeastOneParameter(values);
		List<Object> key = keyFrom(values);
		ensureSameNumberOfValues(key);
		updateCounts(key);
	}

	void updateCounts(List<Object> key) {
		int count = counts.computeIfAbsent(key, any -> 0);
		counts.put(key, ++count);
		statisticsEntries = null;
	}

	private void ensureAtLeastOneParameter(Object[] values) {
		if (Arrays.equals(values, new Object[0])) {
			String message = String.format("StatisticsCollector[%s] must be called with at least one value", label);
			throw new IllegalArgumentException(message);
		}
	}

	private void ensureSameNumberOfValues(List<Object> keyCandidate) {
		if (counts.isEmpty()) {
			return;
		}
		List<Object> anyKey = counts.keySet().iterator().next();
		if (anyKey.size() != keyCandidate.size()) {
			String message = String.format("StatisticsCollector[%s] must always be called with same number of values", label);
			throw new IllegalArgumentException(message);
		}
	}

	private List<Object> keyFrom(Object[] values) {
		if (values != null) {
			return Arrays.asList(values);
		} else {
			return Collections.singletonList(null);
		}
	}

	@Override
	public double percentage(Object... values) {
		List<StatisticsEntry> statistics = statisticsEntries();
		return statistics
				   .stream()
				   .filter(entry -> entry.key.equals(keyFrom(values)))
				   .map(entry -> entry.percentage)
				   .findFirst().orElse(0.0);
	}

	@Override
	public int count() {
		return counts.values().stream().mapToInt(aCount -> aCount).sum();
	}

	@Override
	public int count(Object... values) {
		List<Object> key = keyFrom(values);
		return getCounts().getOrDefault(key, 0);
	}

	public Map<List<Object>, Integer> getCounts() {
		return counts;
	}

	public Tuple2<String, String> createReportEntry(String propertyName) {
		List<StatisticsEntry> statisticsEntries = statisticsEntries();

		int maxKeyLength = statisticsEntries.stream().mapToInt(entry -> entry.name.length()).max().orElse(0);
		boolean fullNumbersOnly = statisticsEntries.stream().noneMatch(entry -> entry.percentage < 1);

		int sum = count();
		StringBuilder statistics = new StringBuilder();
		final int decimals = (int) Math.max(1, Math.round(Math.log10(sum)));
		for (StatisticsEntry statsEntry : statisticsEntries) {
			statistics.append(formatEntry(statsEntry, maxKeyLength, fullNumbersOnly, decimals));
		}

		String keyStatistics = String.format("[%s] (%d) %s", propertyName, sum, label);
		return Tuple.of(keyStatistics, statistics.toString());
	}

	private List<StatisticsEntry> statisticsEntries() {
		if (statisticsEntries != null) {
			return statisticsEntries;
		}
		statisticsEntries = calculateStatistics();
		return statisticsEntries;
	}

	private List<StatisticsEntry> calculateStatistics() {
		int sum = count();
		return counts.entrySet()
					 .stream()
					 .sorted(this::compareStatisticsEntries)
					 .filter(entry -> !entry.getKey().equals(Collections.emptyList()))
					 .map(entry -> {
						 double percentage = entry.getValue() * 100.0 / sum;
						 return new StatisticsEntry(entry.getKey(), displayKey(entry.getKey()), entry
																									.getValue(), percentage);
					 })
					 .collect(Collectors.toList());
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
		return key.stream().map(Objects::toString).collect(Collectors.joining(" "));
	}

	private static class StatisticsEntry {
		private List<Object> key;
		private final String name;
		private long count;
		private final double percentage;

		StatisticsEntry(List<Object> key, String name, long count, double percentage) {
			this.key = key;
			this.name = name;
			this.count = count;
			this.percentage = percentage;
		}
	}
}
