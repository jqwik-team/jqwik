package net.jqwik.engine.execution.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

class PropertyLifecycleTests {

	@Property
	void coveringFailureInAfter(@ForAll @Positive int anInt) {
		PropertyLifecycle.after(
			(executionResult, context) ->
				PropertyExecutionResult.successful(executionResult.getSeed().orElse(null)));

		fail("should be covered by after hook");
	}

	@Group
	class Stores {

		private Store<Integer> outsideSum = PropertyLifecycle.store("outsideSum", () -> 0);

		@Example
		void outsideStoreHasLocalScope() {
			outsideSum.update(outsideSum -> outsideSum + 1);
			PropertyLifecycle.onSuccess( () -> assertThat(outsideSum.get()).isEqualTo(1));
		}

		@Example
		void outsideStoreHasLocalScopeCounterPart() {
			outsideSum.update(outsideSum -> outsideSum + 1000);
			PropertyLifecycle.onSuccess( () -> assertThat(outsideSum.get()).isEqualTo(1000));
		}

		@Property(tries = 100, generation = GenerationMode.RANDOMIZED)
		void insideStoreHasLocalScope(@ForAll @IntRange(min = 1, max = 10) int anInt) {
			Store<Integer> sum = PropertyLifecycle.store("sum", () -> 0);
			sum.update(before -> before + anInt);

			PropertyLifecycle.onSuccess(
				() -> {
					assertThat(sum.get()).isGreaterThanOrEqualTo(100);
					assertThat(sum.get()).isLessThanOrEqualTo(1000);
				}
			);
		}

		@Example
		void insideStoreHasLocalScopeCounterPart(@ForAll @IntRange(min = 42, max = 42) int anInt) {
			Store<Integer> sum = PropertyLifecycle.store("sum", () -> 0);
			sum.update(before -> anInt);

			PropertyLifecycle.onSuccess(
				() -> {
					assertThat(sum.get()).isEqualTo(42);
				}
			);
		}
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
