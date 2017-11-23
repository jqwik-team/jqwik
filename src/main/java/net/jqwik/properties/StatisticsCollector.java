package net.jqwik.properties;

import org.junit.platform.engine.reporting.*;

import java.util.*;
import java.util.function.*;

public class StatisticsCollector {

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
		Map<String, String> values = new HashMap<>();
		long sum = counts.values().stream().mapToLong(aLong -> aLong).sum();
		counts.forEach((key, value) -> {
			int percentage = Math.round((value * 100) / sum);
			values.put(key.toString(), percentage + " %");
		});
		return ReportEntry.from(values);
	}

	public void collect(Object value) {
		Long count = counts.computeIfAbsent(value, key -> 0L);
		counts.put(value, ++count);
	}
}
