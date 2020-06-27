package net.jqwik.engine;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;
import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

import static net.jqwik.api.lifecycle.PropertyExecutionResult.Status.*;

/**
 * Used to annotate methods that are expected to fail.
 * Useful for testing jqwik itself
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AddLifecycleHook(ExpectFailure.Hook.class)
public @interface ExpectFailure {

	class NullChecker implements Consumer<PropertyExecutionResult> {
		@Override
		public void accept(PropertyExecutionResult propertyExecutionResult) {
		}
	}

	class NoFailure extends Throwable {
	}

	/**
	 * Optionally specify a checker
	 */
	Class<? extends Consumer<PropertyExecutionResult>> checkResult() default NullChecker.class;

	String value() default "";

	Class<? extends Throwable> failureType() default NoFailure.class;

	class Hook implements AroundPropertyHook {

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
			PropertyExecutionResult testExecutionResult = property.execute();
			Consumer<PropertyExecutionResult> resultChecker = getResultChecker(context.targetMethod(), context.testInstance());
			String messageFromAnnotation = getMessage(context.targetMethod());
			Class<? extends Throwable> expectedFailureType = getFailureType(context.targetMethod());

			try {
				if (testExecutionResult.status() == FAILED) {
					checkFailureType(expectedFailureType, testExecutionResult);
					resultChecker.accept(testExecutionResult);
					return testExecutionResult.mapToSuccessful();
				}
			} catch (AssertionError assertionError) {
				return testExecutionResult.mapToFailed(assertionError);
			}

			String headerText = messageFromAnnotation == null ? "" : messageFromAnnotation + "\n\t";
			String reason = testExecutionResult.throwable()
											   .map(throwable -> String.format("it failed with [%s]", throwable))
											   .orElse("it did not fail at all");
			String message = String.format(
				"%sProperty [%s] should have failed with failure of type %s, but %s",
				headerText,
				context.label(),

				reason
			);
			return testExecutionResult.mapToFailed(message);
		}

		private void checkFailureType(Class<? extends Throwable> expectedFailureType, PropertyExecutionResult testExecutionResult) {
			if (expectedFailureType.equals(NoFailure.class)) {
				return;
			}
			testExecutionResult.throwable().ifPresent(throwable -> {
				if (!expectedFailureType.isAssignableFrom(throwable.getClass())) {
					throw new AssertionFailedError("Wrong failure type: " + throwable);
				}
			});
			if (!testExecutionResult.throwable().isPresent()) {
				throw new AssertionFailedError("No failure exception");
			}
		}

		private String getMessage(Method method) {
			Optional<ExpectFailure> annotation = AnnotationSupport.findAnnotation(method, ExpectFailure.class);
			return annotation.map(expectFailure -> {
				String message = expectFailure.value();
				return message.isEmpty() ? null : message;
			}).orElse(null);
		}

		private Class<? extends Throwable> getFailureType(Method method) {
			Optional<ExpectFailure> annotation = AnnotationSupport.findAnnotation(method, ExpectFailure.class);
			if (annotation.isPresent()) {
				return annotation.get().failureType();
			} else {
				return Throwable.class;
			}
		}

		private Consumer<PropertyExecutionResult> getResultChecker(Method method, Object testInstance) {
			Optional<ExpectFailure> annotation = AnnotationSupport.findAnnotation(method, ExpectFailure.class);
			return annotation.map((ExpectFailure expectFailure) -> {
				Class<? extends Consumer<PropertyExecutionResult>> checkResult = expectFailure.checkResult();
				return (Consumer<PropertyExecutionResult>) JqwikReflectionSupport.newInstanceInTestContext(checkResult, testInstance);
			})
							 .orElse(
								 JqwikReflectionSupport.newInstanceInTestContext(ExpectFailure.NullChecker.class, testInstance)
							 );
		}

		@Override
		public int aroundPropertyProximity() {
			return Hooks.AroundProperty.EXPECT_FAILURE_PROXIMITY;
		}

	}
}