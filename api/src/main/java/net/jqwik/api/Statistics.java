package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This class serves as a container for static methods to collect statistical
 * data about generated values within a property method.
 */
@API(status = MAINTAINED, since = "1.0")
public class Statistics {

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
		 *               a key for the reported table of frequencies. Constraints:
		 *               <ul>
		 *               <li>There must be at least one value</li>
		 *               <li>The number of values for the same collector (i.e. same label)
		 *               must always be the same in a single property</li>
		 *               <li>Values can be {@code null}</li>
		 *               </ul>
		 * @throws IllegalArgumentException if one of the constraints on {@code values} is violated
		 */
		void collect(Object... values);

		/**
		 * Calculate the percentage of occurrences of a given
		 * {@code values} combination.
		 * Since the value changes from try to try it's most often used in a call
		 * to {@linkplain PropertyLifecycle#after(PropertyLifecycle.AfterPropertyExecutor)}.
		 *
		 * @param values Can be anything. Must be equal to the values used in {@linkplain #collect(Object...)}
		 * @return The percentage between 0.0 and 100.0
		 */
		@API(status = EXPERIMENTAL, since = "1.2.3")
		double percentage(Object... values);

		/**
		 * Count all calls to {@linkplain #collect(Object...)}.
		 * Since the value changes from try to try it's most often used in a call
		 * to {@linkplain PropertyLifecycle#after(PropertyLifecycle.AfterPropertyExecutor)}.
		 *
		 * @return The count is 0 or larger
		 */
		@API(status = EXPERIMENTAL, since = "1.2.3")
		int count();

		/**
		 * Count all occurrences of a given {@code values} combination.
		 * Since the value changes from try to try it's most often used in a call
		 * to {@linkplain PropertyLifecycle#after(PropertyLifecycle.AfterPropertyExecutor)}.
		 *
		 * @param values Can be anything. Must be equal to the values used in {@linkplain #collect(Object...)}
		 * @return The count is 0 or larger
		 */
		@API(status = EXPERIMENTAL, since = "1.2.3")
		int count(Object... values);

		/**
		 * Perform coverage checking for successful property on statistics
		 * for values collected with {@linkplain #collect(Object...)}
		 *
		 * @param checker Code that consumes a {@linkplain StatisticsCoverage} object
		 */
		@API(status = EXPERIMENTAL, since = "1.2.3")
		void coverage(Consumer<StatisticsCoverage> checker);

	}

	@API(status = EXPERIMENTAL, since = "1.2.3")
	public interface StatisticsCoverage {

		@API(status = EXPERIMENTAL, since = "1.2.3")
		interface CoverageChecker {
			void count(Predicate<Integer> countChecker);
		}

		CoverageChecker check(Object... values);
	}

	public static abstract class StatisticsFacade {
		private static StatisticsFacade implementation;

		static {
			implementation = FacadeLoader.load(StatisticsFacade.class);
		}

		public abstract StatisticsCollector collectorByLabel(String label);

		public abstract StatisticsCollector defaultCollector();

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
	 * Calculate the percentage of occurrences of a given
	 * {@code values} combination.
	 * Since the value changes from try to try it's most often used in a call
	 * to {@linkplain net.jqwik.api.lifecycle.PropertyLifecycle#after(PropertyLifecycle.AfterPropertyExecutor)}.
	 *
	 * @param values Can be anything. Must be equal to the values used in {@linkplain #collect(Object...)}
	 * @return The percentage between 0.0 and 100.0
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
	public static double percentage(Object... values) {
		return StatisticsFacade.implementation.defaultCollector().percentage(values);
	}

	/**
	 * Call this method to get a labeled instance of {@linkplain StatisticsCollector}.
	 *
	 * @param label The label will be used for reporting the collected statistical values
	 */
	@API(status = EXPERIMENTAL, since = "1.2.0")
	public static StatisticsCollector label(String label) {
		return StatisticsFacade.implementation.collectorByLabel(label);
	}

	/**
	 * Count all calls to {@linkplain #collect(Object...)}.
	 * Since the value changes from try to try it's most often used in a call
	 * to {@linkplain PropertyLifecycle#after(PropertyLifecycle.AfterPropertyExecutor)}.
	 *
	 * @return The count is 0 or larger
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
	public static int count() {
		return StatisticsFacade.implementation.defaultCollector().count();
	}

	/**
	 * Count all occurrences of a given {@code values} combination.
	 * Since the value changes from try to try it's most often used in a call
	 * to {@linkplain PropertyLifecycle#after(PropertyLifecycle.AfterPropertyExecutor)}.
	 *
	 * @param values Can be anything. Must be equal to the values used in {@linkplain #collect(Object...)}
	 * @return The count is 0 or larger
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
	public static int count(Object... values) {
		return StatisticsFacade.implementation.defaultCollector().count(values);
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
