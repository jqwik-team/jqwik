package net.jqwik.engine.support;

import java.lang.reflect.*;

public class JqwikLambdaSupport {

	private JqwikLambdaSupport() {}

	/**
	 * This method is used in arbitrary implementations of equals() to allow memoization of generators.
	 *
	 * Comparing two lambdas by their implementation class works if they don't access an enclosing object's state.
	 * When in doubt, fail comparison.
	 **/
	public static boolean areEqual(Object l1, Object l2) {
		if (l1 == l2) return true;
		Class<?> l1Class = l1.getClass();
		if (l1Class != l2.getClass()) return false;
		// Check enclosed state
		for (Field field : l1Class.getDeclaredFields()) {
			if (!fieldIsEqualIn(field, l1, l2)) {
				return false;
			}
		}
		return true;
	}

	private static boolean fieldIsEqualIn(Field field, Object left, Object right) {
		field.setAccessible(true);
		try {
			// If field is a functional type use areEqual.
			// TODO: Could there be circular references among functional types?
			if (JqwikReflectionSupport.isFunctionalType(field.getType())) {
				return areEqual(field.get(left), field.get(right));
			}
			return field.get(left).equals(field.get(right));
		} catch (IllegalAccessException e) {
			return false;
		}
	}
}
