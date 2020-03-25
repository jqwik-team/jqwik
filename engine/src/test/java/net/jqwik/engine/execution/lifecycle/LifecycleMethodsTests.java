package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

/**
 * This test class makes only sense as a whole. Running individual methods
 * will fail in the after container hook.
 */
@AddLifecycleHook(AssertCalls.class)
class LifecycleMethodsTests extends LifecycleMethodsTestsSuper {
	static List<String> calls = new ArrayList<>();

	@BeforeContainer
	static void beforeContainer() {
		calls.add("before container");
	}

	@BeforeProperty
	void beforeProperty() {
		calls.add("before property");
	}

	@BeforeExample
	void beforeExample() {
		calls.add("before example");
	}

	@BeforeTry
	void beforeTry() {
		calls.add("before try");
	}

	@Group
	class Inner {
		@BeforeProperty
		void beforeInnerProperty() {
			calls.add("before inner property");
		}

		@Property(tries = 1)
		void innerProperty() {
			calls.add("inner try");
		}

		@AfterProperty
		void afterInnerProperty() {
			calls.add("after inner property");
		}

	}

	@Property(tries = 2)
	void property1(@ForAll int anInt) {
		calls.add("try 1");
	}

	@Property(tries = 2)
	void property2(@ForAll int anInt) {
		calls.add("try 2");
	}

	@AfterTry
	void afterTry() {
		calls.add("after try");
	}

	@AfterExample
	void afterExample() {
		calls.add("after example");
	}

	@AfterProperty
	void afterProperty() {
		calls.add("after property");
	}

	@AfterContainer
	static void afterContainer() {
		calls.add("after container");
	}

}

class LifecycleMethodsTestsSuper {
	@BeforeContainer
	static void beforeContainerSuper() {
		LifecycleMethodsTests.calls.add("before container super");
	}

	@BeforeProperty
	void beforePropertySuper() {
		LifecycleMethodsTests.calls.add("before property super");
	}

	@AfterProperty
	void afterPropertySuper() {
		LifecycleMethodsTests.calls.add("after property super");
	}

	@AfterContainer
	static void afterContainerSuper() {
		LifecycleMethodsTests.calls.add("after container super");
	}
}

class AssertCalls implements AfterContainerHook {

	@Override
	public void afterContainer(ContainerLifecycleContext context) {

		Assertions.assertThat(LifecycleMethodsTests.calls).containsExactly(
			"before container super",
			"before container",
			"before property super",
			"before example",
			"before property",
			"before inner property",
			"before try",
			"inner try",
			"after try",
			"after inner property",
			"after example",
			"after property",
			"after property super",
			"before property super",
			"before example",
			"before property",
			"before try",
			"try 1",
			"after try",
			"before try",
			"try 1",
			"after try",
			"after example",
			"after property",
			"after property super",
			"before property super",
			"before example",
			"before property",
			"before try",
			"try 2",
			"after try",
			"before try",
			"try 2",
			"after try",
			"after example",
			"after property",
			"after property super",
			"after container",
			"after container super"
		);

	}

	@Override
	public int afterContainerProximity() {
		return -50;
	}
}
