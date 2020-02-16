package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

class AroundTryHookTests {

	static int count1 = 0;

	@Property(tries = 10)
	@AddLifecycleHook(IncrementCount1.class)
	@PerProperty(AssertCount1.class)
	void singleHookIsApplied() {
	}

	class AssertCount1 implements PerProperty.PerPropertyLifecycle {
		@Override
		public void onSuccess() {
			assertThat(count1).isEqualTo(10);
		}
	}

	static int count2 = 0;

	@Property(tries = 10)
	@AddLifecycleHook(IncrementCount2.class)
	@AddLifecycleHook(IncrementCount2.class)
	@PerProperty(AssertCount2.class)
	void sameHookTwiceIsIgnored() {
		PropertyLifecycle.onSuccess(() -> {
			assertThat(count2).isEqualTo(10);
		});
	}

	class AssertCount2 implements PerProperty.PerPropertyLifecycle {
		@Override
		public void onSuccess() {
			assertThat(count2).isEqualTo(10);
		}
	}

	static int count3 = 0;

	@Property(tries = 10)
	@AddLifecycleHook(SwallowFailure.class)
	@PerProperty(AssertCount3.class)
	void hookCanSwallowFailures() {
		count3++;
		if (count3 > 5) {
			fail("Should be swallowed");
		}
	}

	class AssertCount3 implements PerProperty.PerPropertyLifecycle {
		@Override
		public void onSuccess() {
			assertThat(count3).isEqualTo(10);
		}
	}

	static int sum = 0;

	@Property(tries = 10)
	@AddLifecycleHook(ChangeParamTo1.class)
	@PerProperty(AssertSum.class)
	void hookCanChangeParameters(@ForAll int anInt) {
		sum += anInt;
	}

	class AssertSum implements PerProperty.PerPropertyLifecycle {
		@Override
		public void onSuccess() {
			assertThat(sum).isEqualTo(10);
		}
	}

	static int finishEarlyTries = 0;

	@Property(tries = 10)
	@AddLifecycleHook(FinishAfter5Tries.class)
	@PerProperty(AssertFinishEarlyTries.class)
	void hookCanFinishEarly(@ForAll int anInt) {
		finishEarlyTries += 1;
	}

	class AssertFinishEarlyTries implements PerProperty.PerPropertyLifecycle {
		@Override
		public void onSuccess() {
			assertThat(finishEarlyTries).isEqualTo(5);
		}
	}

}

class IncrementCount1 implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
		AroundTryHookTests.count1++;
		return aTry.execute(parameters);
	}
}

class IncrementCount2 implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
		AroundTryHookTests.count2++;
		return aTry.execute(parameters);
	}
}

class SwallowFailure implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
		aTry.execute(parameters);
		return TryExecutionResult.satisfied();
	}
}

class ChangeParamTo1 implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
		List<Object> changedParameters = Arrays.asList(1);
		return aTry.execute(changedParameters);
	}
}

class FinishAfter5Tries implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
		aTry.execute(parameters);
		boolean finishEarly = AroundTryHookTests.finishEarlyTries >= 5;
		return TryExecutionResult.satisfied(finishEarly);
	}
}