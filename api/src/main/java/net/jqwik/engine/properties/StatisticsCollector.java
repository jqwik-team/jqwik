package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.engine.reporting.*;

public class StatisticsCollector {

	public static final String KEY_STATISTICS = "collected statistics";
	private static ThreadLocal<StatisticsCollector> collector = ThreadLocal.withInitial(StatisticsCollector::new);

	public static void clearAll() {
		collector.remove();
	}

	public static StatisticsCollector get() {
		return collector.get();
	}

	public static void report(Consumer<ReportEntry> reporter) {
		StatisticsCollector collector = get();
		if (collector.isEmpty())
			return;
		reporter.accept(collector.createReportEntry());
	}

	private final Map<List<Object>, Integer> counts = new HashMap<>();

	private boolean isEmpty() {
		return counts.isEmpty();
	}

	public Map<List<Object>, Integer> getCounts() {
		return counts;
	}

	public ReportEntry createReportEntry() {
		StringBuilder statistics = new StringBuilder();
		int sum = counts.values().stream().mapToInt(aCount -> aCount).sum();
		List<StatisticsEntry> statisticsEntries = counts.entrySet().stream() //
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) //
				.filter(entry -> !entry.getKey().equals(Collections.emptyList())) //
				.map(entry -> new StatisticsEntry(displayKey(entry.getKey()), entry.getValue() * 100.0 / sum)) //
				.collect(Collectors.toList());
		int maxKeyLength = statisticsEntries.stream().mapToInt(entry -> entry.name.length()).max().orElse(0);
		boolean fullNumbersOnly = !statisticsEntries.stream().anyMatch(entry -> entry.percentage < 1);

		statisticsEntries.stream() //
				.forEach(statsEntry -> {
					statistics.append(String.format("%n    %1$-" + maxKeyLength + "s : %2$s %%", //
							statsEntry.name, //
							displayPercentage(statsEntry.percentage, fullNumbersOnly)));
				});
		return ReportEntry.from(KEY_STATISTICS, statistics.toString());
	}

	private String displayPercentage(double percentage, boolean fullNumbersOnly) {
		if (fullNumbersOnly)
			return String.valueOf(Math.round(percentage));
		return String.valueOf(Math.round(percentage * 100.0) / 100.0);
	}

	private String displayKey(List<Object> key) {
		return key.stream().map(Object::toString).collect(Collectors.joining(" "));
	}

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

	static class StatisticsEntry {
		private final String name;
		private final double percentage;

		StatisticsEntry(String name, double percentage) {
			this.name = name;
			this.percentage = percentage;
		}
	}
}
