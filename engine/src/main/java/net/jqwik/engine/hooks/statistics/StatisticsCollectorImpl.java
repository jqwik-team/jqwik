package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;
import net.jqwik.api.statistics.StatisticsCoverage.*;

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

	private void updateCounts(List<Object> key) {
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

	// Currently only used for testing
	public double percentage(Object... values) {
		return statisticsEntry(values).percentage;
	}

	private StatisticsEntry statisticsEntry(Object[] values) {
		return statisticsEntries()
				   .stream()
				   .filter(entry -> entry.key.equals(keyFrom(values)))
				   .findFirst()
				   .orElse(StatisticsEntry.NULL);
	}

	private StatisticsEntry query(Predicate<List<Object>> query) {
		return statisticsEntries()
				   .stream()
				   .filter(entry -> {
					   List<Object> values = entry.key;
					   return query.test(values);
				   })
				   .reduce(StatisticsEntry.NULL, (aggregate, entry) -> aggregate.plus(entry));
	}

	public int countAllCollects() {
		return counts.values().stream().mapToInt(aCount -> aCount).sum();
	}

	// Currently only used for testing
	public int count(Object... values) {
		return statisticsEntry(values).count;
	}

	@Override
	public void coverage(Consumer<StatisticsCoverage> checker) {
		PropertyLifecycle.onSuccess(Tuple.of(this, checker.getClass()), () -> {
			StatisticsCoverage coverage = new StatisticsCoverageImpl();
			checker.accept(coverage);
		});
	}

	public Map<List<Object>, Integer> getCounts() {
		return counts;
	}

	List<StatisticsEntry> statisticsEntries() {
		if (statisticsEntries != null) {
			return statisticsEntries;
		}
		statisticsEntries = calculateStatistics();
		return statisticsEntries;
	}

	private List<StatisticsEntry> calculateStatistics() {
		int sum = countAllCollects();
		return counts.entrySet()
					 .stream()
					 .sorted(this::compareStatisticsEntries)
					 .filter(entry -> !entry.getKey().equals(Collections.emptyList()))
					 .map(entry -> {
						 double percentage = entry.getValue() * 100.0 / sum;
						 return new StatisticsEntry(
							 entry.getKey(),
							 displayKey(entry.getKey()),
							 entry.getValue(), percentage
						 );
					 })
					 .collect(Collectors.toList());
	}

	private int compareStatisticsEntries(Map.Entry<List<Object>, Integer> e1, Map.Entry<List<Object>, Integer> e2) {
		List<Object> k1 = e1.getKey();
		List<Object> k2 = e2.getKey();
		if (k1.size() != k2.size()) {
			return Integer.compare(k1.size(), k2.size());
		}
		return e2.getValue().compareTo(e1.getValue());
	}

	private String displayKey(List<Object> key) {
		return key.stream().map(Objects::toString).collect(Collectors.joining(" "));
	}

	String label() {
		return label;
	}

	private class StatisticsCoverageImpl implements StatisticsCoverage {

		@Override
		public CoverageChecker check(Object... values) {
			StatisticsEntry entry = statisticsEntry(values);
			return new CoverageCheckerImpl(entry, countAllCollects());
		}

		@Override
		public CoverageChecker checkQuery(Predicate<? extends List<?>> query) {
			@SuppressWarnings("unchecked")
			StatisticsEntry entry = query((Predicate<List<Object>>) query);
			return new CoverageCheckerImpl(entry, countAllCollects());
		}
	}

	private static class CoverageCheckerImpl implements CoverageChecker {

		private final StatisticsEntry entry;
		private final int countAll;

		public CoverageCheckerImpl(StatisticsEntry entry, int countAll) {
			this.entry = entry;
			this.countAll = countAll;
		}

		@Override
		public void count(Predicate<Integer> countChecker) {
			if (!countChecker.test(entry.count)) {
				String message = String.format("Count of %s does not fulfill condition", entry.count);
				fail(message);
			}
		}

		@Override
		public void count(BiPredicate<Integer, Integer> countChecker) {
			if (!countChecker.test(entry.count, countAll)) {
				String message = String.format("Count of (%s, %s) does not fulfill condition", entry.count, countAll);
				fail(message);
			}
		}

		@Override
		public void count(Consumer<Integer> countChecker) {
			count(c -> {
				countChecker.accept(c);
				return true;
			});
		}

		@Override
		public void count(BiConsumer<Integer, Integer> countChecker) {
			count((c, a) -> {
				countChecker.accept(c, a);
				return true;
			});
		}

		@Override
		public void percentage(Predicate<Double> percentageChecker) {
			if (!percentageChecker.test(entry.percentage)) {
				String message = String.format("Percentage of %s does not fulfill condition", entry.percentage);
				fail(message);
			}
		}

		@Override
		public void percentage(Consumer<Double> percentageChecker) {
			percentage(p -> {
				percentageChecker.accept(p);
				return true;
			});
		}

		private void fail(String message) {
			throw new AssertionFailedError(message);
		}

	}
}
