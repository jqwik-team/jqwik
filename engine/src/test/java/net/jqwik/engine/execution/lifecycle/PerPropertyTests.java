package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.PerProperty.*;
import net.jqwik.engine.*;

import static org.assertj.core.api.Assertions.*;

class PerPropertyTests {

	@Property
	@PerProperty(CoverFailure.class)
	void coverFailure_in_onFailure(@ForAll @Positive int anInt) {
		fail("should be covered by onFailure");
	}

	class CoverFailure implements Lifecycle {
		@Override
		public PropertyExecutionResult onFailure(PropertyExecutionResult propertyExecutionResult) {
			return propertyExecutionResult.mapToSuccessful();
		}
	}

	@ExpectFailure
	@Property(tries = 10)
	@PerProperty(FailInOnSuccess.class)
	void fail_in_onSuccess(@ForAll @Positive int anInt) {
	}

	class FailInOnSuccess implements Lifecycle {
		@Override
		public void onSuccess() {
			fail("failing in onSuccess");
		}
	}

	private boolean beforeCalled = false;
	@Property
	@PerProperty(CheckCallToBefore.class)
	void before_is_called_with_PropertyLifecycleContext() {

	}

	class CheckCallToBefore implements Lifecycle {
		@Override
		public void before(PropertyLifecycleContext context) {
			assertThat(context.testInstance()).isInstanceOf(PerPropertyTests.class);
			beforeCalled = true;
		}

		@Override
		public void onSuccess() {
			assertThat(beforeCalled).isTrue();
		}
	}


	private static boolean afterCalled = false;

	@Property
	@PerProperty(CallAfter.class)
	@AddLifecycleHook(AssertAfterCalled.class)
	void after_is_called_when_succeeding() {
		afterCalled = false;
	}

	@Property
	@PerProperty(CallAfter.class)
	@AddLifecycleHook(AssertAfterCalled.class)
	void after_is_called_when_failing() {
		afterCalled = false;
	}

	class CallAfter implements Lifecycle {

		@Override
		public PropertyExecutionResult onFailure(PropertyExecutionResult propertyExecutionResult) {
			return propertyExecutionResult.mapToSuccessful();
		}

		@Override
		public void after(PropertyExecutionResult result) {
			afterCalled = true;
		}
	}

	static class AssertAfterCalled implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
			try {
				return property.execute();
			} finally {
				assertThat(afterCalled).describedAs("after should have been called").isTrue();
			}
		}
	}

	@Property(tries = 10)
	@PerProperty(ResolveStringToAString.class)
	void resolveParameters(@ForAll int anInt, String aString) {
		assertThat(aString).isEqualTo("a string");
	}

	private class ResolveStringToAString implements Lifecycle {
		@Override
		public Optional<Supplier<Object>> resolve(
			ParameterResolutionContext parameterContext,
			PropertyLifecycleContext propertyContext
		) {
			if (parameterContext.typeUsage().isOfType(String.class)) {
				return Optional.of(() -> "a string");
			}
			return Optional.empty();
		}
	}
}
