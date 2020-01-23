package net.jqwik.engine;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;

public class ExpectFailureHook implements AroundPropertyHook {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		PropertyExecutionResult testExecutionResult = property.execute();
		Class<? extends Throwable> expectedFailureType = getExpectedFailureType(context.targetMethod());
		String messageFromAnnotation = getMessage(context.targetMethod());

		if (testExecutionResult.getStatus() == PropertyExecutionResult.Status.FAILED) {
			if (expectedFailureType == null) {
				return testExecutionResult.changeToSuccessful();
			}
			if (resultHasExpectedFailureType(testExecutionResult, expectedFailureType)) {
				return testExecutionResult.changeToSuccessful();
			}
		}

		String headerText = messageFromAnnotation == null ? "" : messageFromAnnotation + "\n\t";
		String expectedFailureTypeText = expectedFailureType == null ? ""
											 : String.format(" with exception of type [%s]", expectedFailureType.getName());
		String reason = testExecutionResult.getThrowable()
										   .map(throwable -> String.format("it failed with [%s]", throwable))
										   .orElse("it did not fail at all");
		String message = String.format(
			"%sProperty [%s] should have failed%s, but %s",
			headerText,
			context.label(),
			expectedFailureTypeText,
			reason
		);
		return testExecutionResult.changeToFailed(message);
	}

	private String getMessage(Method method) {
		Optional<ExpectFailure> annotation = AnnotationSupport.findAnnotation(method, ExpectFailure.class);
		return annotation.map(expectFailure -> {
			String message = expectFailure.value();
			return message.isEmpty() ? null : message;
		}).orElse(null);
	}

	private Boolean resultHasExpectedFailureType(
		PropertyExecutionResult testExecutionResult,
		Class<? extends Throwable> expectedFailureType
	) {
		return testExecutionResult.getThrowable()
								  .map(throwable -> expectedFailureType.isAssignableFrom(throwable.getClass()))
								  .orElse(false);
	}

	private Class<? extends Throwable> getExpectedFailureType(Method method) {
		Optional<ExpectFailure> annotation = AnnotationSupport.findAnnotation(method, ExpectFailure.class);
		return annotation.map(expectFailure -> {
			Class<? extends Throwable> expectedFailureType = expectFailure.throwable();
			return expectedFailureType == ExpectFailure.None.class ? null : expectedFailureType;
		}).orElse(null);
	}

	@Override
	public int aroundPropertyProximity() {
		return Hooks.AroundProperty.EXPECT_FAILURE_PROXIMITY;
	}

}
