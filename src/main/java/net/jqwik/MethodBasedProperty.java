
package net.jqwik;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.gen5.commons.util.ReflectionUtils;

public class MethodBasedProperty implements ExecutableProperty {

	private final Class<?> testClass;
	private final Method propertyMethod;
	private final Set<Generator> generators = new HashSet<>();

	public MethodBasedProperty(Class<?> testClass, Method propertyMethod) {
		this.testClass = testClass;
		this.propertyMethod = propertyMethod;
		generators.add(new IntegerGenerator());
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
		return (boolean) ReflectionUtils.invokeMethod(propertyMethod, testInstance, resolveParams());
	}

	private Object[] resolveParams() {
		return Arrays.stream(propertyMethod.getParameters()) //
		.map(parameter -> {
			Generator generator = findGenerator(parameter.getType()).orElseThrow(() -> {
				String message = String.format("Cannot generate values for paramters of type '%s'",
					parameter.getType());
				return new RuntimeException(message);
			});
			return generator.generate();
		}).toArray();
	}

	private Optional<Generator> findGenerator(Class<?> type) {
		return generators.stream().filter(generator -> generator.canServeType(type)).findFirst();
	}
}
