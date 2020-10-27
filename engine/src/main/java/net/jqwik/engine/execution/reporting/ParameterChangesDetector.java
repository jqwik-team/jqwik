package net.jqwik.engine.execution.reporting;

import net.jqwik.api.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

class ParameterChangesDetector {

	static boolean haveParametersChanged(List<Object> before, List<Object> after) {
		return atLeastOneChangedParameterHasEqualsImplementation(before, after);
	}

	private static boolean atLeastOneChangedParameterHasEqualsImplementation(List<Object> before, List<Object> after) {
		List<Boolean> hasEqualsImplementation = before.stream()
													  .map(Object::getClass)
													  .map(ParameterChangesDetector::hasOwnEqualsImplementation)
													  .collect(Collectors.toList());

		for (int i = 0; i < hasEqualsImplementation.size(); i++) {
			if (hasEqualsImplementation.get(i) && !Objects.equals(before.get(i), after.get(i))) {
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
