package net.jqwik.api.statistics;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This class serves as an interface to collect statistical
 * data about generated values within a property method.
 */
@API(status = MAINTAINED, since = "1.2.3")
public interface StatisticsCollector {

	/**
	 * Call this method to record an entry for statistical data about generated values.
	 * As soon as this method is called at least once in a property method,
	 * the statistical data will be reported after the property has finished.
	 *
	 * <p>
	 * For examples see {@linkplain Statistics#collect(Object...)}
	 * </p>
	 *
	 * @param values Can be anything. The list of these values is considered
	 *               a key for the reported table of frequencies. Constraints:
	 *               <ul>
	 *               <li>There must be at least one value</li>
	 *               <li>The number of values for the same collector (i.e. same label)
	 *               must always be the same in a single property</li>
	 *               <li>Values can be {@code null}</li>
	 *               </ul>
	 * @return The current instance of collector to allow a fluent coverage API
	 * @throws IllegalArgumentException if one of the constraints on {@code values} is violated
	 */
	StatisticsCollector collect(@Nullable Object... values);

	/**
	 * Perform coverage checking for successful property on statistics.
	 *
	 * <p>
	 * For examples see {@linkplain Statistics#coverage(Consumer)}
	 * </p>
	 *
	 * @param checker Code that consumes a {@linkplain StatisticsCoverage} object
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	void coverage(Consumer<StatisticsCoverage> checker);

}
