package net.jqwik.api;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

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

	/**
	 * Calculate the percentage of occurrences of a given
	 * {@code values} combination.
	 * Since the value changes from try to try it's most of used in a call
	 * to {@linkplain net.jqwik.api.lifecycle.PropertyLifecycle#after(PropertyLifecycle.AfterPropertyExecutor)}.
	 *
	 * @param values Can be anything. Must be equal to the values used in {@linkplain #collect(Object...)}
	 * @return The percentage between 0.0 and 100.0
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
	double percentage(Object... values);
}
