package examples.docs.lifecycle;

import java.util.concurrent.atomic.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

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

	@Override
	public void close() {
		System.out.println("Teardown in close()");
	}

	private static class Count10Tries implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(
			PropertyLifecycleContext propertyDescriptor,
			PropertyExecutor property
		) throws Throwable {
			System.out.println("Before around counting: " + propertyDescriptor.label());
			PropertyExecutionResult testExecutionResult = property.execute();
			System.out.println("After around counting: " + propertyDescriptor.label());

			TestsWithPropertyLifecycle testInstance = (TestsWithPropertyLifecycle) propertyDescriptor.testInstance();
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
			PropertyLifecycleContext propertyDescriptor,
			PropertyExecutor property
		) throws Throwable {
			System.out.println("Before around all: " + propertyDescriptor.label());
			PropertyExecutionResult testExecutionResult = property.execute();
			System.out.println("After around all: " + propertyDescriptor.label());
			return testExecutionResult;
		}
	}
}
