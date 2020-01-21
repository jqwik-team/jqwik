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

	/**
	 * This class serves as an interface to collect statistical
	 * data about generated values within a property method.
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
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
		 * Perform coverage checking for successful property on statistics.
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

			/**
			 * Check the number of occurrences returning true (ok) or false (fail)
			 *
			 * @param countChecker a predicate to accept a selected value set's number of occurrences
			 */
			void count(Predicate<Integer> countChecker);

			/**
			 * Check the number of occurrences using one or more assertions.
			 *
			 * @param countChecker a consumer to accept a selected value set's number of occurrences
			 */
			void count(BiPredicate<Integer, Integer> countChecker);

			/**
			 * Check the number of occurrences returning true (ok) or false (fail).
			 *
			 * @param countChecker a predicate to accept a selected value set's number of occurrences
			 *                     and the count of all submitted value sets to compare with
			 *                     or make a calculation
			 */
			void count(Consumer<Integer> countChecker);

			/**
			 * Check the number of occurrences using one or more assertions.
			 *
			 * @param countChecker a predicate to accept a selected value set's number of occurrences
			 *                     and the count of all submitted value sets to compare with
			 *                     or make a calculation
			 */
			void count(BiConsumer<Integer, Integer> countChecker);

			/**
			 * Check the percentage of occurrences returning true (ok) or false (fail)
			 *
			 * @param percentageChecker a predicate to accept a selected value set's
			 *                          percentage (0.0 - 100.0) of occurrences
			 */
			void percentage(Predicate<Double> percentageChecker);

			/**
			 * Check the number of occurrences returning true (ok) or false (fail).
			 *
			 * @param percentageChecker a predicate to accept a selected value set's
			 *                          percentage (0.0 - 100.0) of occurrences
			 */
			void percentage(Consumer<Double> percentageChecker);

		}

		/**
		 * Select a specific values set for coverage checking.
		 *
		 * @param values Can be anything. Must be equal to the values used in {@linkplain #collect(Object...)}
		 */
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

	/**
	 * Perform coverage checking for successful property on labelled statistics
	 * for values collected with {@linkplain #collect(Object...)}
	 *
	 * @param label   The label that was used for reporting the collected statistical values
	 * @param checker Code that consumes a {@linkplain StatisticsCoverage} object
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
	public static void coverageOf(String label, Consumer<StatisticsCoverage> checker) {
		StatisticsFacade.implementation.collectorByLabel(label).coverage(checker);
	}

}
