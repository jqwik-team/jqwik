package net.jqwik.api.lifecycle.hooks;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PerProperty.*;

public class PerPropertyHook implements AroundPropertyHook, ResolveParameterHook {

	Store<Lifecycle> lifecycle;

	@Override
	public void prepareFor(LifecycleContext context) {
		Optional<PerProperty> perProperty = context.findAnnotation(PerProperty.class);
		Class<? extends Lifecycle> lifecycleClass = perProperty.map(PerProperty::value).orElseThrow(() -> {
			String message = "@PerProperty annotation MUST have a value() attribute";
			return new JqwikException(message);
		});

		lifecycle = Store.create("lifecycle", Lifespan.PROPERTY, () -> context.newInstance(lifecycleClass));
	}

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		runBeforeExecutionLifecycles(context, lifecycle.get());
		PropertyExecutionResult executionResult = property.execute();
		return runAfterExecutionLifecycles(lifecycle.get(), executionResult);
	}

	private void runBeforeExecutionLifecycles(PropertyLifecycleContext context, Lifecycle lifecycle) {
		lifecycle.before(context);
	}

	private PropertyExecutionResult runAfterExecutionLifecycles(
		Lifecycle lifecycle,
		PropertyExecutionResult executionResult
	) {
		try {
			if (executionResult.status() == PropertyExecutionResult.Status.SUCCESSFUL) {
				try {
					lifecycle.onSuccess();
				} catch (Throwable throwable) {
					return executionResult.mapToFailed(throwable);
				}
			} else if (executionResult.status() == PropertyExecutionResult.Status.FAILED) {
				return lifecycle.onFailure(executionResult);
			}
			return executionResult;
		} finally {
			lifecycle.after(executionResult);
		}
	}

	@Override
	public int aroundPropertyProximity() {
		// Somewhat closer than standard hooks
		return 10;
	}

	@Override
	public Optional<Supplier<Object>> resolve(
		ParameterResolutionContext parameterContext,
		PropertyLifecycleContext propertyContext
	) {
		return lifecycle.get().resolve(parameterContext, propertyContext);
	}
}
