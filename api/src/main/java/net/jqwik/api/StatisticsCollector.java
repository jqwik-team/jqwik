package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This class serves as an interface to collect statistical
 * data about generated values within a property method.
 */
@API(status = EXPERIMENTAL, since = "1.2.0")
public interface StatisticsCollector {

	/**
	 * Call this method to record an entry for statistical data about generated values.
	 * As soon as this method is called at least once in a property method,
	 * the statistical data will be reported after the property has finished.
	 *
	 * @param values Can be anything. The list of these values is considered
	 *               a key for the reported table of frequencies.
	 */
	void collect(Object... values);
}
