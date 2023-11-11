package net.jqwik.engine.hooks.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.support.hierarchical.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

public class PropertyLifecycleMethodsHook implements AroundPropertyHook {

	private void beforeProperty(PropertyLifecycleContext context) {
		List<Method> beforeContainerMethods = LifecycleMethods.findBeforePropertyMethods(context.containerClass());
		callPropertyMethods(beforeContainerMethods, context);
	}

	private void callPropertyMethods(List<Method> methods, PropertyLifecycleContext context) {
		List<Object> testInstances = context.testInstances();
		ThrowableCollector throwableCollector = new ThrowableCollector(ignore -> false);
		for (Method method : methods) {
			Object[] parameters = MethodParameterResolver.resolveParameters(method, context);
			throwableCollector.execute(() -> callMethod(method, testInstances, parameters));
		}
		throwableCollector.assertEmpty();
	}

	private void callMethod(Method method, List<Object> containerInstances, Object[] parameters) {
		JqwikReflectionSupport.invokeMethodOnContainer(method, containerInstances, parameters);
	}

	private void afterProperty(PropertyLifecycleContext context) {
		List<Method> afterContainerMethods = LifecycleMethods.findAfterPropertyMethods(context.containerClass());
		callPropertyMethods(afterContainerMethods, context);
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
		return property.executeAndFinally(
			() -> afterProperty(context)
		);
	}
}
