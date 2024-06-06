package net.jqwik.api.statistics;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Intermediate object to provide statistics coverage checking capabilities
 *
 * @see Statistics#coverage(Consumer)
 */
@API(status = MAINTAINED, since = "1.4.0")
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
		 *                     and the count of all submitted value sets to compare with
		 *                     or make a calculation
		 */
		void count(BiPredicate<Integer, Integer> countChecker);

		/**
		 * Check the number of occurrences returning true (ok) or false (fail).
		 *
		 * @param countChecker a predicate to accept a selected value set's number of occurrences
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
	 * @param values Can be anything. Must be equal to the values used in {@linkplain Statistics#collect(Object...)}
	 */
	CoverageChecker check(Object... values);

	/**
	 * Execute a query for coverage checking.
	 *
	 * @param query A {@link Predicate} that takes the collected values as parameter
	 *              and returns true if the specific values shall be counted.
	 */
	CoverageChecker checkQuery(Predicate<? super List<?>> query);

	/**
	 * Match collected values against a regular expression.
	 * Count all values that match.
	 *
	 * <p>
	 * Values must be instances of type of {@linkplain CharSequence},
	 * e.g. {@linkplain String} or {@linkplain StringBuffer}.
	 * Values of other types never match.
	 *
	 * @param regex A regular expression
	 */
	@API(status = EXPERIMENTAL, since = "1.7.1")
	CoverageChecker checkPattern(String regex);
}
