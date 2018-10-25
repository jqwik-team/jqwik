package net.jqwik.support;

public class MathSupport {
	public static long factorial(long number) {
		long result = 1;

		for (long factor = 2; factor <= number; factor++) {
			result *= factor;
		}

		return result;
	}
}
