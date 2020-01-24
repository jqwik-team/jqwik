package net.jqwik.api.statistics;

public interface StatisticsEntry {
	String name();

	int count();

	double percentage();
}
