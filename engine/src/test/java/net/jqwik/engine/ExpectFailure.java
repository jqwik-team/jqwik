package net.jqwik.engine;

import java.lang.annotation.*;
import java.util.function.*;

import net.jqwik.api.lifecycle.*;

/**
 * Used to annotate methods that are expected to fail.
 * Useful for testing jqwik itself
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AddLifecycleHook(ExpectFailureHook.class)
public @interface ExpectFailure {

	class NullChecker implements Consumer<PropertyExecutionResult> {
		@Override
		public void accept(PropertyExecutionResult propertyExecutionResult) {
		}
	}

	/**
	 * Optionally specify a checker
	 */
	Class<? extends Consumer<PropertyExecutionResult>> checkResult() default NullChecker.class;

	String value() default "";
}
