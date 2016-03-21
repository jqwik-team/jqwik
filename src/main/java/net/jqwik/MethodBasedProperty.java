
package net.jqwik;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.gen5.commons.util.ReflectionUtils;
import org.junit.gen5.commons.util.StringUtils;
import org.opentest4j.AssertionFailedError;

public class MethodBasedProperty implements ExecutableProperty {

	private static final Logger LOG = Logger.getLogger(MethodBasedProperty.class.getName());

	private final Class<?> testClass;
	private final Method propertyMethod;
	private final Set<Generator> generators = new HashSet<>();

	public MethodBasedProperty(Class<?> testClass, Method propertyMethod) {
		this.testClass = testClass;
		this.propertyMethod = propertyMethod;
		generators.add(new IntegerGenerator(new Random()));
	}

	@Override
	public String name() {
		return propertyMethod.getName();
	}

	@Override
	public void evaluate() {
		Generator[] parameterGenerators = getParameterGenerators();
		for (int i = 0; i < 100; i++) {

			Object[] parameters = nextParameters(parameterGenerators);
			if (!evaluate(parameters)) {
				String message = String.format("Failed with parameters: [%s]", parameterDescription(parameters));
				throw new AssertionFailedError(message);
			}
		}
	}

	private boolean evaluate(Object[] parameters) {
		LOG.finest(() -> String.format("Run method '%s' with parameters: [%s]", methodDescription(),
			parameterDescription(parameters)));
		Object testInstance = null;
		if (!ReflectionUtils.isStatic(propertyMethod))
			testInstance = ReflectionUtils.newInstance(testClass);
		return (boolean) ReflectionUtils.invokeMethod(propertyMethod, testInstance, parameters);
	}

	private String parameterDescription(Object[] parameters) {
		Stream<String> parameterStrings = Arrays.stream(parameters).map(Object::toString);
		return StringUtils.join(parameterStrings, ",");
	}

	private String methodDescription() {
		return propertyMethod.getName();
	}

	private Object[] nextParameters(Generator[] parameterGenerators) {
		return Arrays.stream(parameterGenerators) //
		.map(generator -> generator.generate()) //
		.toArray();
	}

	private Generator[] getParameterGenerators() {
		return Arrays.stream(propertyMethod.getParameters()) //
		.map(parameter -> {
			Generator generator = findGenerator(parameter.getType()).orElseThrow(() -> {
				String message = String.format("Cannot generate values for paramters of type '%s'",
					parameter.getType());
				return new RuntimeException(message);
			});
			return generator;
		}).collect(Collectors.toList()).toArray(new Generator[0]);
	}

	private Optional<Generator> findGenerator(Class<?> type) {
		return generators.stream().filter(generator -> generator.canServeType(type)).findFirst();
	}
}
