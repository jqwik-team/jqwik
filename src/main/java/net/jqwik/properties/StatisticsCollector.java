package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.reporting.*;

public class StatisticsCollector {

	public static final String KEY_STATISTICS = "statistics";
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

	private final Map<Object, Long> counts = new HashMap<>();

	private boolean isEmpty() {
		return counts.isEmpty();
	}

	public ReportEntry createReportEntry() {
		StringBuilder statistics = new StringBuilder();
		int maxKeyLength = counts.keySet().stream().mapToInt(k -> k.toString().length()).max().orElse(0);
		long sum = counts.values().stream().mapToLong(aLong -> aLong).sum();
		counts.entrySet().stream() //
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) //
				.forEach(entry -> {
					int percentage = Math.round((entry.getValue() * 100) / sum);
					statistics.append(String.format("%n     %1$-" + maxKeyLength + "s : %2$s %%", entry.getKey().toString(), percentage));
				});
		return ReportEntry.from(KEY_STATISTICS, statistics.toString());
	}

	public void collect(Object value) {
		Long count = counts.computeIfAbsent(value, key -> 0L);
		counts.put(value, ++count);
	}
}
