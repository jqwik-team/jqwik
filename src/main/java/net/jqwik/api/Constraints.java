package net.jqwik.api;

public class Constraints {
	public static void require(boolean condition) {
		if (!condition)
			throw new ParameterConstraintViolation();
	}
}
