package net.jqwik.api.statistics;

/**
 * Describes an entry for a given statistics selector.
 * This is used when plugging in your own statistics report formats.
 *
 * @see StatisticsReportFormat
 */
public interface StatisticsEntry {

	/**
	 * The name of an entry usually refers to the collected value(s)
	 */
	String name();

	/**
	 * The number of times a certain value (set) has been collected
	 */
	int count();

	/**
	 * The percentage of times a certain value (set) has been collected
	 */
	double percentage();
}
