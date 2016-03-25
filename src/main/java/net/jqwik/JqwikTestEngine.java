
package net.jqwik;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.function.Predicate;
import java.util.logging.Logger;

import org.junit.gen5.commons.util.AnnotationUtils;
import org.junit.gen5.commons.util.ReflectionUtils;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.engine.support.descriptor.JavaSource;
import org.junit.gen5.engine.support.hierarchical.HierarchicalTestEngine;

import net.jqwik.api.Property;

public class JqwikTestEngine extends HierarchicalTestEngine<JqwikExecutionContext> {

	private static final Logger LOG = Logger.getLogger(JqwikTestEngine.class.getName());

	private static final String ENGINE_ID = "jqwik";

	public static final String SEGMENT_TYPE_CLASS = "jqwik-class";
	public static final String SEGMENT_TYPE_METHOD = "jqwik-method";
	public static final String SEGMENT_TYPE_SEED = "jqwik-seed";

	// Test runs should produce the same results for one instantiation of the test engine
	private long seed = new Random().nextLong();

	private Random seedGenerator;

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	protected JqwikExecutionContext createExecutionContext(ExecutionRequest request) {
		return new JqwikExecutionContext();
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueEngineId) {
		seedGenerator = new Random(seed);
		JqwikEngineDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueEngineId);
		resolveSelectors(discoveryRequest, engineDescriptor);
		return engineDescriptor;
	}

	private void resolveSelectors(EngineDiscoveryRequest discoveryRequest, JqwikEngineDescriptor engineDescriptor) {
		discoveryRequest.getSelectorsByType(ClassSelector.class).forEach(
			classSelector -> resolveClass(classSelector, engineDescriptor));
	}

	private void resolveClass(ClassSelector classSelector, JqwikEngineDescriptor engineDescriptor) {
		Class<?> testClass = classSelector.getTestClass();
		UniqueId uniqueId = engineDescriptor.getUniqueId().append(SEGMENT_TYPE_CLASS, testClass.getName());
		JqwikClassDescriptor classDescriptor = new JqwikClassDescriptor(uniqueId, testClass);
		resolveClassMethods(testClass, classDescriptor);
		engineDescriptor.addChild(classDescriptor);
	}

	private void resolveClassMethods(Class<?> testClass, JqwikClassDescriptor classDescriptor) {
		Predicate<Method> isPropertyMethod = method -> AnnotationUtils.isAnnotated(method, Property.class);
		ReflectionUtils.findMethods(testClass, isPropertyMethod).forEach(propertyMethod -> {
			if (ReflectionUtils.isPrivate(propertyMethod)) {
				LOG.warning(() -> String.format("Method '%s' cannot be property because it is private", methodDescription(propertyMethod)));
				return;
			}
			if (!isAcceptedPropertyReturnType(propertyMethod.getReturnType())) {
				LOG.warning(() -> String.format("Method '%s' cannot be property because it must return a boolean value", methodDescription(propertyMethod)));
				return;
			}

			long seed = seedGenerator.nextLong();
			UniqueId uniqueId = classDescriptor.getUniqueId().append(SEGMENT_TYPE_METHOD, propertyMethod.getName()).append(SEGMENT_TYPE_SEED, Long.toString(seed));

			int numberOfTrials = propertyMethod.getDeclaredAnnotation(Property.class).trials();

			classDescriptor.addChild(new JqwikPropertyDescriptor(uniqueId, propertyMethod.getName(), new JavaSource(propertyMethod)));
		});

	}

	private boolean isAcceptedPropertyReturnType(Class<?> propertyReturnType) {
		return propertyReturnType.equals(boolean.class) || propertyReturnType.equals(Boolean.class);
	}

	private String methodDescription(Method method) {
		return method.getDeclaringClass().getName() + "#" + method.getName();
	}

}
