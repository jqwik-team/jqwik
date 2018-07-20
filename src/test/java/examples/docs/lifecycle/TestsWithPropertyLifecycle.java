package examples.docs.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import org.assertj.core.api.*;
import org.junit.platform.engine.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

@AddLifecycleHook(TestsWithPropertyLifecycle.AroundAll.class)
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
	// is also declared at class level and therefor ignored:
	@AddLifecycleHook(AroundAll.class)
	void countingTries(@ForAll String aString) {
		counter.incrementAndGet();
	}

	@Override
	public void close() {
		System.out.println("Teardown after each property");
	}

	private static class Count10Tries implements AroundPropertyHook {
		@Override
		public TestExecutionResult aroundProperty(PropertyLifecycleContext propertyDescriptor, Callable<TestExecutionResult> property) throws Exception {
			System.out.println("Before around counting: " + propertyDescriptor.label());
			TestExecutionResult testExecutionResult = property.call();
			System.out.println("After around counting: " + propertyDescriptor.label());

			TestsWithPropertyLifecycle testInstance = (TestsWithPropertyLifecycle) propertyDescriptor.testInstance();
			Assertions.assertThat(testInstance.counter.get()).isEqualTo(10);
			return testExecutionResult;
		}
	}

	static class AroundAll implements AroundPropertyHook {
		@Override
		public TestExecutionResult aroundProperty(PropertyLifecycleContext propertyDescriptor, Callable<TestExecutionResult> property) throws Exception {
			System.out.println("Before around all: " + propertyDescriptor.label());
			TestExecutionResult testExecutionResult = property.call();
			System.out.println("After around all: " + propertyDescriptor.label());
			return testExecutionResult;
		}
	}
}
