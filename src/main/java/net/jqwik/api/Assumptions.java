package net.jqwik.api;

public class Assumptions {
	public static void assume(boolean condition) {
		if (!condition)
			throw new AssumptionViolation();
	}
}
