package net.jqwik.execution;

import org.assertj.core.api.Assertions;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

class AutoCloseableHookTests implements AutoCloseable {

	public static final int STATIC_COUNTER_RESET_VALUE = 11;
	public static final int COUNTER_RESET_VALUE = 22;

	private static int staticCounter = STATIC_COUNTER_RESET_VALUE;
	private int counter;

	AutoCloseableHookTests() {
		counter = COUNTER_RESET_VALUE;
	}

	@Override
	public void close() {
		staticCounter = STATIC_COUNTER_RESET_VALUE;
	}

	@Example
	void firstExampleCountersReset() {
		Assertions.assertThat(staticCounter).isEqualTo(STATIC_COUNTER_RESET_VALUE);
		Assertions.assertThat(counter).isEqualTo(COUNTER_RESET_VALUE);

		staticCounter = 1000;
		counter = 2000;
	}

	@Example
	void secondExampleCountersReset() {
		Assertions.assertThat(staticCounter).isEqualTo(STATIC_COUNTER_RESET_VALUE);
		Assertions.assertThat(counter).isEqualTo(COUNTER_RESET_VALUE);

		staticCounter = 1000;
		counter = 2000;
	}

	@Group
	class InnerContainer {

		@Example
		void innerExampleAlsoCallsOuterClose() {
			Assertions.assertThat(staticCounter).isEqualTo(STATIC_COUNTER_RESET_VALUE);
			Assertions.assertThat(counter).isEqualTo(COUNTER_RESET_VALUE);

			staticCounter = 1000;
			counter = 2000;
		}

	}

	@Property
	void closeIsCalledOnlyOncePerTestMethod(@ForAll @IntRange(min = 1, max = 100) int anInt) {
		Assertions.assertThat(staticCounter).isGreaterThanOrEqualTo(STATIC_COUNTER_RESET_VALUE);
		staticCounter++;
	}

	@Property
	void constructorIsCalledOnlyOncePerTestMethod(@ForAll @IntRange(min = 1, max = 100) int anInt) {
		Assertions.assertThat(counter).isGreaterThanOrEqualTo(COUNTER_RESET_VALUE);
		counter = 1000;
	}
}
