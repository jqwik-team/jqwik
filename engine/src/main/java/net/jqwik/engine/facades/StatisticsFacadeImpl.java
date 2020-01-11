package net.jqwik.engine.facades;

import net.jqwik.api.*;
import net.jqwik.engine.hooks.statistics.*;

/**
 * Is loaded through reflection in api module
 */
public class StatisticsFacadeImpl extends Statistics.StatisticsFacade {

	private static final String DEFAULT_LABEL = "statistics";

	@Override
	public void collect(Object... values) {
		StatisticsCollectorImpl.get(DEFAULT_LABEL).collect(values);
	}

	@Override
	public StatisticsCollector label(String label) {
		return StatisticsCollectorImpl.get(label);
	}
}
