package net.jqwik.testing;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.facades.*;
import net.jqwik.api.lifecycle.*;

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

			String headerText = messageFromAnnotation == null ? "" : messageFromAnnotation + "\n\t";

			try {
				if (testExecutionResult.status() == FAILED) {
					Optional<String> checkFailureResult = checkFailureType(expectedFailureType, testExecutionResult.throwable(), context.label(), headerText);
					if (checkFailureResult.isPresent()) {
						return testExecutionResult.mapToFailed(checkFailureResult.get());
					}
					resultChecker.accept(testExecutionResult);
					return testExecutionResult.mapToSuccessful();
				}
			} catch (AssertionError assertionError) {
				return testExecutionResult.mapToFailed(assertionError);
			}

			String message = createErrorMessage(context.label(), expectedFailureType, headerText, "it did not fail at all");
			return testExecutionResult.mapToFailed(message);
		}

		private String createErrorMessage(
			String label,
			Class<? extends Throwable> expectedFailureType,
			String headerText,
			String reason
		) {
			String message = String.format(
				"%sProperty [%s] should have failed with [%s], but %s",
				headerText,
				label,
				expectedFailureType.getName(),
				reason
			);
			return message;
		}

		private Optional<String> checkFailureType(
			Class<? extends Throwable> expectedFailureType,
			Optional<Throwable> throwable,
			String label,
			String headerText
		) {
			if (expectedFailureType.equals(NoFailure.class)) {
				return Optional.empty();
			}
			if (!throwable.isPresent()) {
				String reason = "it failed without exception";
				return Optional.of(createErrorMessage(label, expectedFailureType, headerText, reason));
			}
			if (!expectedFailureType.isAssignableFrom(throwable.get().getClass())) {
				String reason = String.format("it failed with [%s]", throwable.get());
				return Optional.of(createErrorMessage(label, expectedFailureType, headerText, reason));
			}
			return Optional.empty();
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
			if (annotation.isPresent() && (annotation.get().failureType() != NoFailure.class)) {
				return annotation.get().failureType();
			} else {
				return Throwable.class;
			}
		}

		private Consumer<PropertyExecutionResult> getResultChecker(Method method, Object testInstance) {
			Optional<ExpectFailure> annotation = AnnotationSupport.findAnnotation(method, ExpectFailure.class);
			return annotation.map((ExpectFailure expectFailure) -> {
				Class<? extends Consumer<PropertyExecutionResult>> checkResult = expectFailure.checkResult();
				return (Consumer<PropertyExecutionResult>) ReflectionSupportFacade.implementation
					.newInstanceInTestContext(checkResult, testInstance);
			}).orElse(
				ReflectionSupportFacade.implementation.newInstanceInTestContext(NullChecker.class, testInstance)
			);
		}

		@Override
		public int aroundPropertyProximity() {
			return -95;
		}

	}
}