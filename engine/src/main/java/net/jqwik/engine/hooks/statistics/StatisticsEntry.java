package net.jqwik.engine.hooks.statistics;

import java.util.*;

class StatisticsEntry {
	public static final StatisticsEntry NULL = new StatisticsEntry(null, null, 0, 0.0);

	final List<Object> key;
	final String name;
	final int count;
	final double percentage;

	StatisticsEntry(List<Object> key, String name, int count, double percentage) {
		this.key = key;
		this.name = name;
		this.count = count;
		this.percentage = percentage;
	}

	public StatisticsEntry plus(StatisticsEntry other) {
		int newCount = count + other.count;
		double newPercentage = percentage + other.percentage;
		return new StatisticsEntry(Collections.emptyList(), "adhoc query", newCount, newPercentage);
	}
}
