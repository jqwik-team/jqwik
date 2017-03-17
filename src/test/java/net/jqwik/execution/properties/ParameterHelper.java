package net.jqwik.execution.properties;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParameterHelper {
	public static List<Parameter> getParametersFor(Class<?> aClass, String methodName) {
		return getParameters(getMethod(aClass, methodName));
	}

	private static List<Parameter> getParameters(Method method) {
		return Arrays.stream(method.getParameters()).collect(Collectors.toList());
	}

	private static Method getMethod(Class<?> aClass, String methodName) {
		return Arrays.stream(aClass.getDeclaredMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
	}

}
