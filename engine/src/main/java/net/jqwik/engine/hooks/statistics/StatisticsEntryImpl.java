package net.jqwik.engine.hooks.statistics;

import java.util.*;

import net.jqwik.api.statistics.*;
import net.jqwik.engine.support.*;

public class StatisticsEntryImpl implements StatisticsEntry {
	static StatisticsEntryImpl nullFor(List<Object> values) {
		return new StatisticsEntryImpl(values, JqwikStringSupport.displayString(values), 0, 0.0);
	}

	static StatisticsEntryImpl nullWithName(String name) {
		return new StatisticsEntryImpl(null, name, 0, 0.0);
	}

	private final List<Object> values;
	private final String name;
	private final int count;
	private final double percentage;

	public StatisticsEntryImpl(List<Object> values, String name, int count, double percentage) {
		this.values = values;
		this.name = name;
		this.count = count;
		this.percentage = percentage;
	}

	StatisticsEntryImpl plus(StatisticsEntryImpl other, int totalCount) {
		int newCount = count + other.count;
		double newPercentage = newCount * 100.0 / totalCount;
		return new StatisticsEntryImpl(Collections.emptyList(), name, newCount, newPercentage);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public int count() {
		return count;
	}

	@Override
	public double percentage() {
		return percentage;
	}

	@Override
	public List<Object> values() {
		return values;
	}

	@Override
	public String toString() {
		return String.format("%s (%s, %s%%): %s", name, count, percentage, JqwikStringSupport.displayString(values));
	}
}
