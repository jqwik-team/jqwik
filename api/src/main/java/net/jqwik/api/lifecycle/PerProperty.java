package net.jqwik.api.lifecycle;

import java.lang.annotation.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Annotate property methods of a container class with {@code @PerProperty}
 * if you want to have some lifecycle control over this property alone.
 *
 * <p>
 *     If you want to control the lifecycle of all property methods use
 *     {@linkplain BeforeProperty} or {@linkplain AfterProperty}.
 * </p>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@AddLifecycleHook(PerProperty.PerPropertyHook.class)
@API(status = MAINTAINED, since = "1.2.4")
public @interface PerProperty {

	interface Lifecycle {

		/**
		 * Override if you want to provide parameters for this property.
		 *
		 * @param parameterContext The object to retrieve information about the parameter to resolve
		 * @return a supplier wrapped in {@code Optional.of()}
		 */
		default Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext) {
			return Optional.empty();
		}

		/**
		 * Override if you want to perform some work once before the annotated property (or example).
		 *
		 * @param context The object to retrieve information about the current property
		 */
		default void before(PropertyLifecycleContext context) {}

		/**
		 * Override if you want to perform some work once after the annotated property (or example).
		 *
		 * @param propertyExecutionResult The object to retrieve information about the property's execution result
		 */
		default void after(PropertyExecutionResult propertyExecutionResult) {}

		/**
		 * Override if you want to perform some work or run assertions if - and only if - the property succeeded.
		 * If you want to make the property fail just use an appropriate assertion methods or throw an exception.
		 */
		default void onSuccess() {}

		/**
		 * Override if you want to perform some work or run assertions if - and only if - the property failed.
		 * You have to return the original {@code propertyExecutionResult} or transform it into another result.
		 *
		 * @param propertyExecutionResult The object that represents the property's execution result
		 */
		default PropertyExecutionResult onFailure(PropertyExecutionResult propertyExecutionResult) {
			return propertyExecutionResult;
		}
	}

	/**
	 * Return a class that implements {@linkplain Lifecycle}
	 *
	 * @return An implementation of {@linkplain Lifecycle}
	 */
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
