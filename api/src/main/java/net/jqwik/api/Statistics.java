package net.jqwik.api;

import net.jqwik.engine.properties.*;

public class Statistics {

	private Statistics() {
	}

	public static void collect(Object... values) {
		StatisticsCollector.get().collect(values);
	}

}
