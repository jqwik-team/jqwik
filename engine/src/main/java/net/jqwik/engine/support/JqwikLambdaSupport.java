package net.jqwik.engine.support;

import java.lang.reflect.*;

public class JqwikLambdaSupport {

	private JqwikLambdaSupport() {}

	/**
	 * Comparing two lambdas by their implementation class works if they don't access an enclosing object's state.
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
			return field.get(left).equals(field.get(right));
		} catch (IllegalAccessException e) {
			return false;
		}
	}
}
