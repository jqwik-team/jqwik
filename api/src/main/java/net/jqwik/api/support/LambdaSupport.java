package net.jqwik.api.support;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.support.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public class LambdaSupport {

	private LambdaSupport() {}

	/**
	 * This method is used in arbitrary implementations of equals() to allow memoization of generators.
	 *
	 * Comparing two lambdas by their implementation class works if they don't access an enclosing object's state.
	 * When in doubt, fail comparison.
	 **/
	public static <T> boolean areEqual(T l1, T l2) {
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
			if (isFunctionalType(field.getType())) {
				return areEqual(field.get(left), field.get(right));
			}
			return field.get(left).equals(field.get(right));
		} catch (IllegalAccessException e) {
			return false;
		}
	}

	// TODO: This duplicates JqwikReflectionSupport.isFunctionalType() because module dependencies
	private static boolean isFunctionalType(Class<?> candidateType) {
		if (!candidateType.isInterface()) {
			return false;
		}
		return countInterfaceMethods(candidateType) == 1;
	}

	private static long countInterfaceMethods(Class<?> candidateType) {
		Method[] methods = candidateType.getMethods();
		return findInterfaceMethods(methods).size();
	}

	private static List<Method> findInterfaceMethods(Method[] methods) {
		return Arrays
				   .stream(methods)
				   .filter(m -> !m.isDefault() && !ModifierSupport.isStatic(m))
				   .collect(Collectors.toList());
	}

}
