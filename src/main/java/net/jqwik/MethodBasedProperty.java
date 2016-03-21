package net.jqwik;

import java.lang.reflect.Method;

import org.junit.gen5.commons.util.ReflectionUtils;

public class MethodBasedProperty implements ExecutableProperty {

	private final Class<?> testClass;
	private final Method propertyMethod;

	public MethodBasedProperty(Class<?> testClass, Method propertyMethod) {
		this.testClass = testClass;
		this.propertyMethod = propertyMethod;
	}

	@Override
	public String name() {
		return propertyMethod.getName();
	}

	@Override
	public boolean evaluate() {
		Object testInstance = null;
		if (!ReflectionUtils.isStatic(propertyMethod))
			testInstance = ReflectionUtils.newInstance(testClass);
		return (boolean) ReflectionUtils.invokeMethod(propertyMethod, testInstance);
	}
}
