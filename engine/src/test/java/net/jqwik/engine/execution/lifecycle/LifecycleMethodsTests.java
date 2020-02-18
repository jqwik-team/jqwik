package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@AddLifecycleHook(AssertCalls.class)
class LifecycleMethodsTests extends LifecycleMethodsTestsSuper {
	static List<String> calls = new ArrayList<>();

	@BeforeContainer
	static void beforeContainer() {
		calls.add("before container");
	}

	@BeforeProperty
	static void beforeProperty() {
		calls.add("before property");
	}

	@Property(tries = 2)
	void property1(@ForAll int anInt) {
		calls.add("try 1");
	}

	@Property(tries = 2)
	void property2(@ForAll int anInt) {
		calls.add("try 2");
	}

	@AfterProperty
	static void afterProperty() {
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
			"try 1",
			"try 1",
			"try 2",
			"try 2",
			"after container",
			"after container super"
		);

	}

	@Override
	public int afterContainerProximity() {
		return -50;
	}
}
