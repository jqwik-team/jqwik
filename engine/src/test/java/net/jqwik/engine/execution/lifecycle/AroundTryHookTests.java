package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

class AroundTryHookTests {

	static int count1 = 0;

	@Property(tries = 10)
	@AddLifecycleHook(IncrementCount1.class)
	void singleHookIsApplied() {
		PropertyLifecycle.onSuccess(() -> {
			assertThat(count1).isEqualTo(10);
		});
	}

	static int count2 = 0;

	@Property(tries = 10)
	@AddLifecycleHook(IncrementCount2.class)
	@AddLifecycleHook(IncrementCount2.class)
	void sameHookTwiceIsIgnored() {
		PropertyLifecycle.onSuccess(() -> {
			assertThat(count2).isEqualTo(10);
		});
	}

	static int count3 = 0;

	@Property(tries = 10)
	@AddLifecycleHook(SwallowFailure.class)
	void hookCanSwallowFailures() {
		count3++;
		fail("Should be swallowed");
		PropertyLifecycle.onSuccess(() -> {
			assertThat(count3).isEqualTo(10);
		});
	}

	static int sum = 0;

	@Property(tries = 10)
	@AddLifecycleHook(ChangeParamTo1.class)
	void hookCanChangeParameters(@ForAll int anInt) {
		sum += anInt;
		PropertyLifecycle.onSuccess(() -> {
			assertThat(sum).isEqualTo(10);
		});
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
		try {
			return aTry.execute(parameters);
		} catch (AssertionError ignore) {
			return TryExecutionResult.satisfied();
		}
	}
}

class ChangeParamTo1 implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
		List<Object> changedParameters = Arrays.asList(1);
		return aTry.execute(changedParameters);
	}
}