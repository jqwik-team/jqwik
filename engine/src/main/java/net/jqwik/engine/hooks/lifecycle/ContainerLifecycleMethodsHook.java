package net.jqwik.engine.hooks.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.support.hierarchical.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;

public class ContainerLifecycleMethodsHook implements AroundContainerHook {

	@Override
	public void beforeContainer(ContainerLifecycleContext context) {
		context.containerClass().ifPresent(containerClass -> {
			List<Method> beforeContainerMethods = LifecycleMethods.findBeforeContainerMethods(containerClass);
			callContainerMethods(beforeContainerMethods, context);
		});
	}

	private void callContainerMethods(List<Method> methods, ContainerLifecycleContext context) {
		ThrowableCollector throwableCollector = new ThrowableCollector(ignore -> false);
		for (Method method : methods) {
			Object[] parameters = MethodParameterResolver.resolveParameters(method, context);
			throwableCollector.execute(() -> callStaticMethod(method, parameters));
		}
		throwableCollector.assertEmpty();
	}

	private void callStaticMethod(Method method, Object[] parameters) {
		ReflectionSupport.invokeMethod(method, null, parameters);
	}

	@Override
	public void afterContainer(ContainerLifecycleContext context) {
		context.containerClass().ifPresent(containerClass -> {
			List<Method> afterContainerMethods = LifecycleMethods.findAfterContainerMethods(containerClass);
			callContainerMethods(afterContainerMethods, context);
		});
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	@Override
	public boolean appliesTo(Optional<AnnotatedElement> element) {
		return element.map(e -> e instanceof Class).orElse(false);
	}

	@Override
	public int proximity() {
		return Hooks.AroundContainer.CONTAINER_LIFECYCLE_METHODS_PROXIMITY;
	}
}
