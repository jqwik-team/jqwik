package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This class serves as a container for static methods to collect statistical
 * data about generated values within a property method.
 */
@API(status = MAINTAINED, since = "1.0")
public class Statistics {

	public static abstract class StatisticsFacade {
		private static StatisticsFacade implementation;

		static {
			implementation = FacadeLoader.load(StatisticsFacade.class);
		}

		public abstract void collect(Object... values);

		public abstract double percentage(Object... values);

		public abstract StatisticsCollector label(String label);
	}

	/**
	 * Call this method to get a labeled instance of {@linkplain StatisticsCollector}.
	 *
	 * @param label The label will be used for reporting the collected statistical values
	 */
	@API(status = EXPERIMENTAL, since = "1.2.0")
	public static StatisticsCollector label(String label) {
		return StatisticsFacade.implementation.label(label);
	}

	/**
	 * Call this method to record an entry for statistical data about generated values.
	 * As soon as this method is called at least once in a property method,
	 * the statistical data will be reported after the property has finished.
	 *
	 * @param values Can be anything. The list of these values is considered
	 *               a key for the reported table of frequencies.
	 */
	public static void collect(Object... values) {
		StatisticsFacade.implementation.collect(values);
	}

}
