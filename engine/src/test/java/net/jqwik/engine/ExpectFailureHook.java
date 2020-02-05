package net.jqwik.engine;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

import static net.jqwik.api.lifecycle.PropertyExecutionResult.Status.*;

public class ExpectFailureHook implements AroundPropertyHook {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		PropertyExecutionResult testExecutionResult = property.execute();
		Consumer<PropertyExecutionResult> resultChecker = getResultChecker(context.targetMethod(), context.testInstance());
		String messageFromAnnotation = getMessage(context.targetMethod());

		try {
			if (testExecutionResult.status() == FAILED) {
				resultChecker.accept(testExecutionResult);
				return testExecutionResult.changeToSuccessful();
			}
		} catch (AssertionError assertionError) {
			return testExecutionResult.changeToFailed(assertionError);
		}

		String headerText = messageFromAnnotation == null ? "" : messageFromAnnotation + "\n\t";
		String reason = testExecutionResult.throwable()
										   .map(throwable -> String.format("it failed with [%s]", throwable))
										   .orElse("it did not fail at all");
		String message = String.format(
			"%sProperty [%s] should have failed, but %s",
			headerText,
			context.label(),
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
