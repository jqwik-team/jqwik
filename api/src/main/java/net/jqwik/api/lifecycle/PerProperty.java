package net.jqwik.api.lifecycle;

import java.lang.annotation.*;
import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.hooks.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@AddLifecycleHook(PerPropertyHook.class)
@API(status = EXPERIMENTAL, since = "1.2.4")
public @interface PerProperty {

	interface Lifecycle extends ResolveParameterHook {
		@Override
		default Optional<Supplier<Object>> resolve(
			ParameterResolutionContext parameterContext,
			PropertyLifecycleContext propertyContext
		) {
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
}
