package net.jqwik.engine.hooks.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.support.hierarchical.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

public class BeforeTryMembersHook implements AroundTryHook {

	private void beforeTry(TryLifecycleContext context) {
		System.out.println("initialize before try members");
		// List<Method> beforeTryMethods = LifecycleMethods.findBeforeTryMethods(context.containerClass());
		// callTryMethods(beforeTryMethods, context);
	}

	private void callTryMethods(List<Method> methods, TryLifecycleContext context) {
		Object testInstance = context.testInstance();
		ThrowableCollector throwableCollector = new ThrowableCollector(ignore -> false);
		for (Method method : methods) {
			Object[] parameters = MethodParameterResolver.resolveParameters(method, context);
			throwableCollector.execute(() -> callMethod(method, testInstance, parameters));
		}
		throwableCollector.assertEmpty();
	}

	private void callMethod(Method method, Object target, Object[] parameters) {
		JqwikReflectionSupport.invokeMethodPotentiallyOuter(method, target, parameters);
	}

	private void afterTry(TryLifecycleContext context) {
		List<Method> afterTryMethods = LifecycleMethods.findAfterTryMethods(context.containerClass());
		callTryMethods(afterTryMethods, context);
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
	public int aroundTryProximity() {
		return Hooks.AroundTry.BEFORE_TRY_MEMBERS_PROXIMITY;
	}

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		beforeTry(context);
		try {
			return aTry.execute(parameters);
		} finally {
			afterTry(context);
		}
	}
}
