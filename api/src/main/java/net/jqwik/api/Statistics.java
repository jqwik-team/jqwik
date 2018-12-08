package net.jqwik.api;

import java.util.logging.*;

public class Statistics {

	public static abstract class StatisticsFacade {
		private static final Logger LOG = Logger.getLogger(StatisticsFacade.class.getName());
		private static final String STATISTICS_FACADE_IMPL = "net.jqwik.engine.facades.StatisticsFacadeImpl";
		private static StatisticsFacade implementation;

		static {
			try {
				implementation = (StatisticsFacade) Class.forName(STATISTICS_FACADE_IMPL).newInstance();
			} catch (Exception e) {
				LOG.log(
					Level.SEVERE,
					"Cannot load implementation for " + StatisticsFacade.class.getName(),
					e
				);
			}
		}

		public abstract void collect(Object... values);
	}

	public static void collect(Object... values) {
		StatisticsFacade.implementation.collect(values);
	}

}
