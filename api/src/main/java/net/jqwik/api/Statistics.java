package net.jqwik.api;

/**
 * This class serves as a container for static methods to collect statistical
 * data about generated values within a property method.
 */
public class Statistics {

	public static abstract class StatisticsFacade {
		private static StatisticsFacade implementation;

		static {
			implementation = FacadeLoader.load(StatisticsFacade.class);
		}

		public abstract void collect(Object... values);
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
