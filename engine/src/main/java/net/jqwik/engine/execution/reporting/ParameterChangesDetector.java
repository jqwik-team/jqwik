package net.jqwik.engine.execution.reporting;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

class ParameterChangesDetector {

	static boolean haveParametersChanged(List<Object> before, List<Object> after) {
		return atLeastOneParameterHasChanged(before, after);
	}

	private static boolean atLeastOneParameterHasChanged(List<Object> before, List<Object> after) {
		List<Boolean> hasEqualsImplementation =
				before.stream()
					  .map(o -> Objects.isNull(o) ? Object.class : o.getClass())
					  .map(ParameterChangesDetector::hasOwnEqualsImplementation)
					  .collect(Collectors.toList());

		for (int i = 0; i < hasEqualsImplementation.size(); i++) {
			Object beforeValue = before.get(i);
			Object afterValue = after.get(i);

			if (Objects.isNull(beforeValue) != Objects.isNull(afterValue)) {
				return true;
			}

			if (hasEqualsImplementation.get(i) && !Objects.equals(beforeValue, afterValue)) {
				return true;
			}
		}

		return false;
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
