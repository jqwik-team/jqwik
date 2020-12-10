package net.jqwik.api.time;

import net.jqwik.time.*;

public class Dates {

	private Dates() {
		// Must never be called
	}

	public static DateArbitrary dates() {
		return new DefaultDateArbitrary();
	}

	public static DateArbitrary years() {
		return null;
	}

	public static DateArbitrary months() {
		return null;
	}

	public static DateArbitrary daysOfMonth() {
		return null;
	}

	public static DateArbitrary yearsAndMonths() {
		return null;
	}

	public static DateArbitrary daysOfWeek() {
		return null;
	}

}
