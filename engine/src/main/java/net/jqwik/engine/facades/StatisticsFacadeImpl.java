package net.jqwik.engine.facades;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

/**
 * Is loaded through reflection in api module
 */
public class StatisticsFacadeImpl extends Statistics.StatisticsFacade {
	@Override
	public void collect(Object... values) {
		StatisticsCollectorImpl.get().collect(values);
	}

	@Override
	public StatisticsCollector label(String label) {
		return StatisticsCollectorImpl.get(label);
	}
}
