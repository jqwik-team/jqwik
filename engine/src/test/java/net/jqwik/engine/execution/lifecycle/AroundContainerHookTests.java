package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@AddLifecycleHook(CheckCalls.class)
@AddLifecycleHook(Outer.class)
@AddLifecycleHook(Middle.class)
@AddLifecycleHook(Inner.class)
class AroundContainerHookTests {

	static List<String> calls = new ArrayList<>();

	@Example
	void anExample() {
		calls.add("example");
	}

	@Group
	@AddLifecycleHook(AroundNested.class)
	class NestedContainer {
		@Example
		void nestedExample() {
			calls.add("nested example");
		}
	}
}

class Outer implements AroundContainerHook {
	@Override
	public void beforeContainer(ContainerLifecycleContext context) {
		AroundContainerHookTests.calls.add("before outer");
	}
	@Override
	public void afterContainer(ContainerLifecycleContext context) {
		AroundContainerHookTests.calls.add("after outer");
	}
	@Override
	public int proximity() {
		return 10;
	}
}

class Middle implements AroundContainerHook {
	@Override
	public void beforeContainer(ContainerLifecycleContext context) {
		AroundContainerHookTests.calls.add("before middle");
	}
	@Override
	public void afterContainer(ContainerLifecycleContext context) {
		AroundContainerHookTests.calls.add("after middle");
	}
	@Override
	public int proximity() {
		return 50;
	}
}

class Inner implements AroundContainerHook {
	@Override
	public void beforeContainer(ContainerLifecycleContext context) {
		AroundContainerHookTests.calls.add("before inner");
	}
	@Override
	public void afterContainer(ContainerLifecycleContext context) {
		AroundContainerHookTests.calls.add("after inner");
	}
	@Override
	public int proximity() {
		return 100;
	}
}

class AroundNested implements AroundContainerHook {
	@Override
	public void beforeContainer(ContainerLifecycleContext context) {
		AroundContainerHookTests.calls.add("before nested");
	}
	@Override
	public void afterContainer(ContainerLifecycleContext context) {
		AroundContainerHookTests.calls.add("after nested");
	}
}

class CheckCalls implements AfterContainerHook {

	@Override
	public void afterContainer(ContainerLifecycleContext context) throws Throwable {
		Assertions.assertThat(AroundContainerHookTests.calls).containsExactly(
			"before outer",
			"before middle",
			"before inner",
			"before nested",
			"nested example",
			"after nested",
			"example",
			"after inner",
			"after middle",
			"after outer"
		);
	}
}
