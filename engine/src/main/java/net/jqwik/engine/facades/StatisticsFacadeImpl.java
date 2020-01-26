package net.jqwik.engine.facades;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;
import net.jqwik.engine.hooks.statistics.*;

/**
 * Is loaded through reflection in api module
 */
public class StatisticsFacadeImpl extends Statistics.StatisticsFacade {

	private static final String DEFAULT_LABEL = "statistics";

	@Override
	public StatisticsCollector collectorByLabel(String label) {
		Store<Map<String, StatisticsCollector>> statisticsStore = Store.get(StatisticsCollectorImpl.COLLECTORS_ID);
		return statisticsStore.get().get(label);
	}

	@Override
	public StatisticsCollector defaultCollector() {
		return collectorByLabel(DEFAULT_LABEL);
	}
}
