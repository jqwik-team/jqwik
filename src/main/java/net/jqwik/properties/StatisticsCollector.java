package net.jqwik.properties;

import org.junit.platform.engine.reporting.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
		int maxKeyLength = counts.keySet().stream().mapToInt(k -> displayKey(k).length()).max().orElse(0);
		int sum = counts.values().stream().mapToInt(aCount -> aCount).sum();
		counts.entrySet().stream() //
				.sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) //
				.forEach(entry -> {
					double percentage = entry.getValue() * 100.0 / sum;
					if (entry.getKey().equals(Collections.emptyList())) return;
					statistics.append(String.format("%n     %1$-" + maxKeyLength + "s : %2$s %%", //
							displayKey(entry.getKey()), //
							displayPercentage(percentage)));
				});
		return ReportEntry.from(KEY_STATISTICS, statistics.toString());
	}

	private String displayPercentage(double percentage) {
		if (percentage >= 1)
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
}
