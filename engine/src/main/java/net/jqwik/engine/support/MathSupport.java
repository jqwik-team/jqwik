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

	// From https://rosettacode.org/wiki/Evaluate_binomial_coefficients#Java
	// Faster than return factorial(n) / (factorial(n - k) * factorial(k));
	// Max n = 70
	public static long binomial(int n, int k) {
		if (n > 70) {
			throw new ArithmeticException("MathSupport.binomial() only works till 70");
		}
		if (k > n - k) {
			k = n - k;
		}

		long b = 1;
		for (int i = 1, m = n; i <= k; i++, m--) {
			b = b * m / i;
		}
		return b;
	}

}
