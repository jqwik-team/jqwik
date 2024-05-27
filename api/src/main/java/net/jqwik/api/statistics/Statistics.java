package net.jqwik.api.statistics;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This class serves as a container for static methods to collect statistical
 * data about generated values within a property method and to check coverage
 * of that data.
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
	 * <p>
	 * Usually you call {@link Statistics#collect(Object[])} method with all arbitraries
	 * (parameters passed to test) and than you use {@link Statistics#coverage(Consumer)}
	 * method to ensure that certain count of some value has been tried.
	 * <p>
	 * NOTE: you can give descriptive name to some collections using {@link #label(String)}
	 * method. Usually you make multiple label+collect calls, i.e. for each passed arbitrary
	 * parameter. This way you can provide parameters with descriptive names/labels.
	 * <p>
	 * Complete documentation is found at
	 * <a href="https://jqwik.net/docs/current/user-guide.html#checking-coverage-of-collected-statistics">
	 * the jqwik documentation page for Checking Coverage of Collected Statistics</a>.
	 * <p>
	 * Simple example:
	 *
	 * <pre>
	 * &#064;Property(generation = GenerationMode.RANDOMIZED)
	 * void labeledStatistics(&#064;ForAll &#064;IntRange(min = 1, max = 10) Integer anInt) {
	 * 	String range = anInt &lt; 3 ? "small" : "large";
	 * 	Statistics.label("range").collect(range);
	 * 	Statistics.label("value").collect(anInt);
	 *
	 * 	Statistics.label("range).coverage(
	 * 		coverage -&gt; coverage.check("small").percentage(p -&gt; p &gt; 20.0)
	 *         );
	 * 	Statistics.label("value").coverage(
	 * 		coverage -&gt; coverage.check(0).count(c -&gt; c &gt; 0)
	 *         );
	 * }
	 * </pre>
	 *
	 * @param values Can be anything. The list of these values is considered
	 *               a key for the reported table of frequencies. Constraints:
	 *               <ul>
	 *               <li>There must be at least one value</li>
	 *               <li>The number of values must always be the same in a single property</li>
	 *               <li>Values can be {@code null}</li>
	 *               </ul>
	 * @throws IllegalArgumentException if one of the constraints on {@code values} is violated
	 * @see #label(String)
	 */
	public static void collect(@Nullable Object... values) {
		StatisticsFacade.implementation.defaultCollector().collect(values);
	}

	/**
	 * Call this method to get a labeled instance of {@linkplain StatisticsCollector}.
	 *
	 * @param label The label will be used for reporting the collected statistical values
	 * @see #collect(Object...) javadoc of the collect(Object...) method for example usage
	 */
	public static StatisticsCollector label(String label) {
		return StatisticsFacade.implementation.collectorByLabel(label);
	}

	/**
	 * Perform coverage checking for successful property on statistics
	 * for values collected with {@linkplain #collect(Object...)}
	 * <p>
	 * Sample usage:
	 *
	 * <pre>
	 * Statistics.coverage(coverage -&gt; coverage.check("small").percentage(p -&gt; p &gt; 20.0));
	 * Statistics.coverage(coverage -&gt; coverage.check(0).count(c -&gt; c &gt; 0));
	 * </pre>
	 *
	 * @param checker Code that consumes a {@linkplain StatisticsCoverage} object
	 * @see #collect(Object...) javadoc of the collect(Object...) method for example usage
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	public static void coverage(Consumer<StatisticsCoverage> checker) {
		StatisticsFacade.implementation.defaultCollector().coverage(checker);
	}

}
