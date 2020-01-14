package net.jqwik.engine.facades;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.statistics.*;

/**
 * Is loaded through reflection in api module
 */
public class StatisticsFacadeImpl extends Statistics.StatisticsFacade {

	private static final String DEFAULT_LABEL = "statistics";

	@Override
	public void collect(Object... values) {
		label(DEFAULT_LABEL).collect(values);
	}

	@Override
	public double percentage(Object... values) {
		return label(DEFAULT_LABEL).percentage(values);
	}

	@Override
	public StatisticsCollector label(String label) {
		Store<Map<String, StatisticsCollector>> statisticsStore = Store.get(StatisticsCollectorImpl.STORE_NAME);
		return statisticsStore.get().get(label);
	}
}
