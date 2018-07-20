package examples.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import org.assertj.core.api.Assertions;
import org.junit.platform.engine.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

class TestsWithPropertyLifecycle implements AutoCloseable {

	TestsWithPropertyLifecycle() {
		System.out.println("Before each property");
	}

	private AtomicInteger counter = new AtomicInteger(0);

	@Example void anExample() {
		System.out.println("anExample");
	}

	@Property(tries = 5)
	void aProperty(@ForAll String aString) {
		System.out.println("anProperty: " + aString);
	}

	@Property(tries = 10)
	@AddLifecycleHook(Count10Tries.class)
	void countingTries(@ForAll String aString) {
		counter.incrementAndGet();
	}

	@Override
	public void close() {
		System.out.println("Finally after each property");
	}

	private static class Count10Tries implements AroundPropertyHook {
		@Override
		public TestExecutionResult aroundProperty(PropertyLifecycleContext propertyDescriptor, Callable<TestExecutionResult> property) throws Exception {
			TestExecutionResult testExecutionResult = property.call();
			if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
				Assertions.assertThat(true).isFalse();
				// Assertions.assertThat(counter.get()).isEqualTo(10);
			}
			return testExecutionResult;
		}
	}
}
