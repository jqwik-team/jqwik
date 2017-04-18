package net.jqwik.execution.properties;

import net.jqwik.properties.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public class TestHelper {
	public static List<Parameter> getParametersFor(Class<?> aClass, String methodName) {
		return getParameters(getMethod(aClass, methodName));
	}

	private static List<Parameter> getParameters(Method method) {
		return Arrays.stream(method.getParameters()).collect(Collectors.toList());
	}

	public static Method getMethod(Class<?> aClass, String methodName) {
		return Arrays.stream(aClass.getDeclaredMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
	}

	public static <T> T generate(Arbitrary<T> arbitrary) {
		return arbitrary.generator(1).next(new Random());
	}

}
