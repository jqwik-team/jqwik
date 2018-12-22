package net.jqwik.engine.support;

public class MathSupport {
	public static long factorial(long number) {
		if (number > 20) {
			throw new ArithmeticException("MathSupport.factorial() only works till 20");
		}
		long result = 1;

		for (long factor = 2; factor <= number; factor++) {
			result *= factor;
		}

		return result;
	}
}
