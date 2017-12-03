package net.jqwik.api;

import net.jqwik.properties.*;

public class Statistics {

	private Statistics() {
	}

	public static void collect(Object ... values) {
		StatisticsCollector.get().collect(values);
	}

}
