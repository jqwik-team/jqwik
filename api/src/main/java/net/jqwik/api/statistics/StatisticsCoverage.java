package net.jqwik.api.statistics;

import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.2.3")
public interface StatisticsCoverage {

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
