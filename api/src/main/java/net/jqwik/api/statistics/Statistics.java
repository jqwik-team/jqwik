package net.jqwik.api.statistics;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This class serves as a container for static methods to collect statistical
 * data about generated values within a property method.
 */
@API(status = MAINTAINED, since = "1.2.3")
public class Statistics {

	public static abstract class StatisticsFacade {
		private static StatisticsFacade implementation;

		static {
			implementation = FacadeLoader.load(StatisticsFacade.class);
		}

		public abstract StatisticsCollector collectorByLabel(String label);

		public abstract StatisticsCollector defaultCollector();
	}

	private Statistics() {
	}

	/**
	 * Call this method to record an entry for statistical data about generated values.
	 * As soon as this method is called at least once in a property method,
	 * the statistical data will be reported after the property has finished.
	 *
	 * @param values Can be anything. The list of these values is considered
	 *               a key for the reported table of frequencies. Constraints:
	 *               <ul>
	 *               <li>There must be at least one value</li>
	 *               <li>The number of values must always be the same in a single property</li>
	 *               <li>Values can be {@code null}</li>
	 *               </ul>
	 * @throws IllegalArgumentException if one of the constraints on {@code values} is violated
	 */
	public static void collect(Object... values) {
		StatisticsFacade.implementation.defaultCollector().collect(values);
	}

	/**
	 * Call this method to get a labeled instance of {@linkplain StatisticsCollector}.
	 *
	 * @param label The label will be used for reporting the collected statistical values
	 */
	public static StatisticsCollector label(String label) {
		return StatisticsFacade.implementation.collectorByLabel(label);
	}

	/**
	 * Perform coverage checking for successful property on statistics
	 * for values collected with {@linkplain #collect(Object...)}
	 *
	 * @param checker Code that consumes a {@linkplain StatisticsCoverage} object
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
	public static void coverage(Consumer<StatisticsCoverage> checker) {
		StatisticsFacade.implementation.defaultCollector().coverage(checker);
	}

}
