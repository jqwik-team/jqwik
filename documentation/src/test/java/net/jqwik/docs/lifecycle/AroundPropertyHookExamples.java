package net.jqwik.docs.lifecycle;

import java.util.*;
import java.util.concurrent.atomic.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

@AddLifecycleHook(AroundPropertyHookExamples.AroundAll.class)
class AroundPropertyHookExamples implements AutoCloseable {

	AroundPropertyHookExamples() {
		System.out.println("Before each property");
	}

	private AtomicInteger counter = new AtomicInteger(0);

	@Example void anExample() {
		System.out.println("anExample");
	}

	@Property(tries = 5)
	boolean failingProperty(@ForAll @AlphaChars @StringLength(5) String aString) {
		System.out.println("    failingProperty: " + aString);
		return false;
	}

	@Property(tries = 10)
	@AddLifecycleHook(Count10Tries.class)
	// is also declared at class level and therefor ignored:
	@AddLifecycleHook(AroundAll.class)
	void countingTries(@ForAll String aString) {
		counter.incrementAndGet();
	}

	@Property(tries = 100)
	@AddLifecycleHook(MeasureTime.class)
	void measureTimeSpent(@ForAll JqwikRandom random) throws InterruptedException {
		Thread.sleep(random.nextInt(50));
	}

	@Override
	public void close() {
		System.out.println("Teardown in close()");
	}

	private static class Count10Tries implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(
			PropertyLifecycleContext context,
			PropertyExecutor property
		) throws Throwable {
			System.out.println("Before around counting: " + context.label());
			PropertyExecutionResult testExecutionResult = property.execute();
			System.out.println("After around counting: " + context.label());

			AroundPropertyHookExamples testInstance = (AroundPropertyHookExamples) context.testInstance();
			assertThat(testInstance.counter.get()).isEqualTo(10);
			return testExecutionResult;
		}

		@Override
		public int aroundPropertyProximity() {
			// Will be called closer to actual property execution
			return 1;
		}
	}

	static class AroundAll implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(
			PropertyLifecycleContext context,
			PropertyExecutor property
		) {
			System.out.println("Before around all: " + context.label());
			PropertyExecutionResult testExecutionResult = property.execute();
			System.out.println("After around all: " + context.label());
			return testExecutionResult;
		}

		@Override
		public PropagationMode propagateTo() {
			return PropagationMode.ALL_DESCENDANTS;
		}
	}

	static class MeasureTime implements AroundPropertyHook {

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
			long before = System.currentTimeMillis();
			PropertyExecutionResult executionResult = property.execute();
			long after = System.currentTimeMillis();
			context.reporter().publishValue("time", String.format("%d ms", after - before));
			return executionResult;
		}
	}
}
