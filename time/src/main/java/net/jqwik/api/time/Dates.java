package net.jqwik.api.time;

import net.jqwik.time.*;

public class Dates {

	private Dates() {
		// Must never be called
	}

	public static DateArbitrary dates() {
		return new DefaultDateArbitrary();
	}
}
