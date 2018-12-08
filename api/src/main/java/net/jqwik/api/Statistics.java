package net.jqwik.api;

public class Statistics {

	public static abstract class StatisticsFacade {
		private static StatisticsFacade implementation;

		static {
			implementation = FacadeLoader.load(StatisticsFacade.class);
		}

		public abstract void collect(Object... values);
	}

	public static void collect(Object... values) {
		StatisticsFacade.implementation.collect(values);
	}

}
