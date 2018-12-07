package net.jqwik.api;

public class Statistics {

	public static abstract class StatisticsFacade {
		private static final String STATISTICS_FACADE_IMPL = "net.jqwik.engine.facades.StatisticsFacadeImpl";
		private static StatisticsFacade implementation;

		static {
			try {
				implementation = (StatisticsFacade) Class.forName(STATISTICS_FACADE_IMPL).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public abstract void collect(Object... values);
	}

	public static void collect(Object... values) {
		StatisticsFacade.implementation.collect(values);
	}

}
