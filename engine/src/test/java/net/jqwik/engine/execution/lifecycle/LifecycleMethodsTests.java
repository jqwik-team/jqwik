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

	@AfterContainer
	static void afterContainer() {
		calls.add("after container");
	}

	@Property(tries = 5)
	void property(@ForAll int anInt) {
		calls.add("try");
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
			"try",
			"try",
			"try",
			"try",
			"try"
//			"after container",
//			"after container super"
		);

	}

	@Override
	public int afterContainerProximity() {
		return -50;
	}
}
