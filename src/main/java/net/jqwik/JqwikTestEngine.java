
package net.jqwik;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import org.junit.gen5.commons.util.ReflectionUtils;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.engine.support.descriptor.JavaSource;
import org.junit.gen5.engine.support.hierarchical.HierarchicalTestEngine;

public class JqwikTestEngine extends HierarchicalTestEngine<JqwikExecutionContext> {

	private static final String ENGINE_ID = "jqwik";

	public static final String SEGMENT_TYPE_CLASS = "jqwik-class";

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
		Predicate<Method> isPropertyMethod = method -> Property.class.isAssignableFrom(method.getReturnType());
		ReflectionUtils.findMethods(testClass, isPropertyMethod).forEach(method -> {
			Property property = createProperty(testClass, method);
			UniqueId uniqueId = classDescriptor.getUniqueId().append("jqwik-property", property.name());
			classDescriptor.addChild(new JqwikPropertyDescriptor(uniqueId, property, new JavaSource(method)));
		});

	}

	private Property createProperty(Class<?> testClass, Method method) {
		Object testInstance = null;
		if (!ReflectionUtils.isStatic(method))
			testInstance = ReflectionUtils.newInstance(testClass);
		return (Property) ReflectionUtils.invokeMethod(method, testInstance);
	}

}
