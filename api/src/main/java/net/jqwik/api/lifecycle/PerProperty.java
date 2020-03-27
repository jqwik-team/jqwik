package net.jqwik.api.lifecycle;

import java.lang.annotation.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@AddLifecycleHook(PerProperty.PerPropertyHook.class)
@API(status = EXPERIMENTAL, since = "1.2.4")
public @interface PerProperty {

	interface Lifecycle {
		default Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext) {
			return Optional.empty();
		}

		default void before(PropertyLifecycleContext context) {}

		default void after(PropertyExecutionResult propertyExecutionResult) {}

		default void onSuccess() {}

		default PropertyExecutionResult onFailure(PropertyExecutionResult propertyExecutionResult) {
			return propertyExecutionResult;
		}
	}

	Class<? extends Lifecycle> value();

	class PerPropertyHook implements AroundPropertyHook, ResolveParameterHook {

		private Lifecycle lifecycle(LifecycleContext context) {
			return Store.getOrCreate("lifecycle", Lifespan.PROPERTY, () -> createLifecycleInstance(context)).get();
		}

		private Lifecycle createLifecycleInstance(LifecycleContext context) {
			Optional<PerProperty> perProperty = context.findAnnotation(PerProperty.class);
			Class<? extends Lifecycle> lifecycleClass = perProperty.map(PerProperty::value).orElseThrow(() -> {
				String message = "@PerProperty annotation MUST have a value() attribute";
				return new JqwikException(message);
			});
			return context.newInstance(lifecycleClass);
		}

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
			Lifecycle lifecycle = lifecycle(context);
			runBeforeExecutionLifecycles(context, lifecycle);
			PropertyExecutionResult executionResult = property.execute();
			return runAfterExecutionLifecycles(lifecycle, executionResult);
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
		public Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext, LifecycleContext lifecycleContext) {
			return lifecycle(lifecycleContext).resolve(parameterContext);
		}
	}
}
