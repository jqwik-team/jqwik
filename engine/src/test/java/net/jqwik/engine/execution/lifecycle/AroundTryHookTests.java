package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

@SuppressLogging
class AroundTryHookTests {

	static int count1 = 0;

	@Property(tries = 10)
	@AddLifecycleHook(IncrementCount1.class)
	@PerProperty(AssertCount1.class)
	void singleHookIsApplied() {
	}

	private class AssertCount1 implements PerProperty.Lifecycle {
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
	}

	private class AssertCount2 implements PerProperty.Lifecycle {
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

	private class AssertCount3 implements PerProperty.Lifecycle {
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

	private class AssertSum implements PerProperty.Lifecycle {
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

	private class AssertFinishEarlyTries implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(finishEarlyTries).isEqualTo(5);
		}
	}

	@Property(tries = 10)
	@AddLifecycleHook(FalsifyThirdTry.class)
	@ExpectFailure(checkResult = ThirdFailedAndShrunkTo0.class)
	void hookCanFalsifyTryAndBeShrinked(@ForAll int anInt) {
	}

	private class ThirdFailedAndShrunkTo0 implements Consumer<PropertyExecutionResult> {
		@Override
		public void accept(PropertyExecutionResult result) {
			assertThat(result.countTries()).isEqualTo(3);
			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).containsExactly(0);
		}
	}

	@Property(tries = 10)
	@AddLifecycleHook(InvalidateEverySecondTry.class)
	@PerProperty(AssertTriesAndChecks.class)
	void hookCanInvalidateData(@ForAll int anInt) {
	}

	@Property(tries = 10)
	@AddLifecycleHook(InvalidateEverySecondTryWithAssumption.class)
	@PerProperty(AssertTriesAndChecks.class)
	void hookCanInvalidateDataWithAssumption(@ForAll int anInt) {
	}

	private class AssertTriesAndChecks implements PerProperty.Lifecycle {
		@Override
		public void after(PropertyExecutionResult result) {
			assertThat(result.countTries()).isEqualTo(10);
			assertThat(result.countChecks()).isEqualTo(5);
		}
	}

	@Property(tries = 2)
	@AddLifecycleHook(CheckTryLifecycleContext.class)
	void checkTryLifecycleContextAttributes() {
		// All checking is done in the hook
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
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		AroundTryHookTests.count2++;
		return aTry.execute(parameters);
	}
}

class SwallowFailure implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		aTry.execute(parameters);
		return TryExecutionResult.satisfied();
	}
}

class ChangeParamTo1 implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		List<Object> changedParameters = Arrays.asList(1);
		return aTry.execute(changedParameters);
	}
}

class FinishAfter5Tries implements AroundTryHook {
	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		aTry.execute(parameters);
		boolean finishEarly = AroundTryHookTests.finishEarlyTries >= 5;
		return TryExecutionResult.satisfied(finishEarly);
	}
}

class FalsifyThirdTry implements AroundTryHook {

	Store<Integer> count = Store.create("count", Lifespan.PROPERTY, () -> 0);

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
		count.update(i -> i + 1);
		TryExecutionResult result = aTry.execute(parameters);
		if (count.get() >= 3) {
			fail("falsified 3rd try");
		}
		return result;
	}
}

class InvalidateEverySecondTry implements AroundTryHook {

	Store<Integer> count = Store.create("count", Lifespan.PROPERTY, () -> 0);

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		count.update(i -> i + 1);
		TryExecutionResult result = aTry.execute(parameters);
		if (count.get() % 2 == 0) {
			return TryExecutionResult.invalid();
		}
		return result;
	}
}

class InvalidateEverySecondTryWithAssumption implements AroundTryHook {

	Store<Integer> count = Store.create("count", Lifespan.PROPERTY, () -> 0);

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		count.update(i -> i + 1);
		TryExecutionResult result = aTry.execute(parameters);
		if (count.get() % 2 == 0) {
			Assume.that(false);
		}
		return result;
	}
}

class CheckTryLifecycleContext implements AroundTryHook {

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable {
		assertThat(context.label()).isEqualTo("checkTryLifecycleContextAttributes");
		assertThat(context.containerClass()).isEqualTo(AroundTryHookTests.class);
		assertThat(context.targetMethod().getName()).isEqualTo("checkTryLifecycleContextAttributes");
		assertThat(context.testInstance()).isInstanceOf(AroundTryHookTests.class);
		assertThat(context.testInstances().get(0)).isInstanceOf(AroundTryHookTests.class);
		return aTry.execute(parameters);
	}
}
