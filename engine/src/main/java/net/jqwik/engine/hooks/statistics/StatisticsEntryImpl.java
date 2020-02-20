package net.jqwik.engine.hooks.statistics;

import java.util.*;

import net.jqwik.api.statistics.*;
import net.jqwik.engine.support.*;

class StatisticsEntryImpl implements StatisticsEntry {
	public static final StatisticsEntryImpl NULL = new StatisticsEntryImpl(null, null, 0, 0.0);

	public static StatisticsEntryImpl nullFor(List<Object> key) {
		return new StatisticsEntryImpl(key, JqwikStringSupport.displayString(key), 0 , 0.0);
	}

	final List<Object> key;
	private final String name;
	private final int count;
	private final double percentage;

	StatisticsEntryImpl(List<Object> key, String name, int count, double percentage) {
		this.key = key;
		this.name = name;
		this.count = count;
		this.percentage = percentage;
	}

	public StatisticsEntryImpl plus(StatisticsEntryImpl other) {
		int newCount = count + other.count;
		double newPercentage = percentage + other.percentage;
		return new StatisticsEntryImpl(Collections.emptyList(), "<adhoc query>", newCount, newPercentage);
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
}
