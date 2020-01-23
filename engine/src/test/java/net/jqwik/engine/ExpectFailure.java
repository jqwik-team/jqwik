package net.jqwik.engine;

import java.lang.annotation.*;

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

	class None extends Throwable {
		private None() {
		}
	}

	/**
	 * Optionally specify an expected Throwable subtype to show up as failure reason.
	 */
	Class<? extends Throwable> throwable() default None.class;

	String value() default "";
}
