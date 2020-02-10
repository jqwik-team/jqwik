package net.jqwik.engine.execution.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

class PropertyLifecycleTests {

	@Property
	void coveringFailureInAfter(@ForAll @Positive int anInt) {
		PropertyLifecycle.after(
			(executionResult, context) -> executionResult.mapToSuccessful());

		fail("should be covered by after hook");
	}

	@Group
	class AfterMethodsAreCalledOnSuccess implements AutoCloseable {

		private boolean setByAfter = false;

		@Override
		public void close() {
			assertThat(setByAfter).isTrue();
		}

		@Example
		void after() {
			PropertyLifecycle.after((propertyExecutionResult, propertyLifecycleContext)-> {
				setByAfter = true;
				return propertyExecutionResult;
			});
		}

		@Example
		void onSuccess() {
			PropertyLifecycle.onSuccess(()-> {
				setByAfter = true;
			});
		}
	}
}
