package net.jqwik.engine.hooks.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.support.hierarchical.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

public class PropertyLifecycleMethodsHook implements AroundPropertyHook {

	private void beforeProperty(PropertyLifecycleContext context) {
		List<Method> beforeContainerMethods = LifecycleMethods.findBeforePropertyMethods(context.containerClass());
		callPropertyMethods(beforeContainerMethods, context.testInstance());
	}

	private void callPropertyMethods(List<Method> methods, Object testInstance) {
		ThrowableCollector throwableCollector = new ThrowableCollector(ignore -> false);
		for (Method method : methods) {
			throwableCollector.execute(() -> callMethod(method, testInstance));
		}
		throwableCollector.assertEmpty();
	}

	private void callMethod(Method method, Object target) {
		JqwikReflectionSupport.invokeMethodPotentiallyOuter(method, target);
	}

	private void afterProperty(PropertyLifecycleContext context) {
		List<Method> afterContainerMethods = LifecycleMethods.findAfterPropertyMethods(context.containerClass());
		callPropertyMethods(afterContainerMethods, context.testInstance());
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	@Override
	public boolean appliesTo(Optional<AnnotatedElement> element) {
		return element.map(e -> e instanceof Method).orElse(false);
	}

	@Override
	public int aroundPropertyProximity() {
		return Hooks.AroundProperty.PROPERTY_LIFECYCLE_METHODS_PROXIMITY;
	}

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
		beforeProperty(context);
		try {
			return property.execute();
		} finally {
			afterProperty(context);
		}
	}
}
