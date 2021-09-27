package net.jqwik.api.statistics;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Describes an entry for a given statistics selector.
 * This is used when plugging in your own statistics report formats.
 *
 * @see StatisticsReportFormat
 */
@API(status = MAINTAINED, since = "1.4.0")
@NonNullApi
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

	/**
	 * The values collected during {@linkplain Statistics#collect(Object...)}
	 */
	List<Object> values();
}
