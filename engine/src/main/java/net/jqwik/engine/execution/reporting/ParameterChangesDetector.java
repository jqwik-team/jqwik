package net.jqwik.engine.execution.reporting;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;

class ParameterChangesDetector {

	static boolean haveParametersChanged(List<Object> before, List<Object> after) {
		return atLeastOneParameterHasChanged(before, after);
	}

	private static boolean atLeastOneParameterHasChanged(List<Object> before, List<Object> after) {
		if (before.size() != after.size()) {
			return true;
		}
		for (int i = 0; i < before.size(); i++) {
			Object beforeValue = before.get(i);
			Object afterValue = after.get(i);
			if (valuesDiffer(beforeValue, afterValue)) {
				return true;
			}
		}
		return false;
	}

	private static boolean valuesDiffer(Object before, Object after) {
		if (Objects.isNull(before) != Objects.isNull(after)) {
			return true;
		}
		if (Objects.isNull(before)) {
			return false;
		}
		if (before.getClass() != after.getClass()) {
			return true;
		}

		if (hasOwnEqualsImplementation(before.getClass())) {
			return !Objects.equals(before, after);
		} else {
			return false;
		}
	}

	private static boolean hasOwnEqualsImplementation(Class<?> aClass) {
		// TODO: There are probably other pathological cases of classes with equals implementation
		if (Proxy.isProxyClass(aClass)) {
			return false;
		}
		return !equalsMethod(aClass).equals(equalsMethod(Object.class));
	}

	private static Method equalsMethod(Class<?> aClass) {
		try {
			return aClass.getMethod("equals", Object.class);
		} catch (NoSuchMethodException e) {
			throw new JqwikException("All classes should have an equals() method");
		}
	}

}
