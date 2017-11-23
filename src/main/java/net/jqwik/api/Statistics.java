package net.jqwik.api;

import net.jqwik.properties.*;

public class Statistics {
	public static void collect(Object value) {
		StatisticsCollector.get().collect(value);
	}

}
