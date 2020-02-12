package net.jqwik.engine.execution.lifecycle;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

@Group
@AddLifecycleHook(CheckAfter.class)
class AfterContainerHookTests {
	static int after = 0;

	@Group
	@AddLifecycleHook(IncrementAfter.class)
	class NestedTests {

		@Example
		void test1() {
			assertThat(after).isEqualTo(0);
		}

		@Example
		void test2() {
			assertThat(after).isEqualTo(0);
		}

	}
}

class CheckAfter implements AfterContainerHook {

	@Override
	public void afterContainer(ContainerLifecycleContext context) {
		assertThat(CurrentTestDescriptor.get()).isInstanceOf(TestDescriptor.class);
		assertThat(AfterContainerHookTests.after).isEqualTo(1);
		assertThat(context.containerClass()).isPresent();
		assertThat(context.containerClass().get()).isEqualTo(AfterContainerHookTests.class);
	}
}

class IncrementAfter implements AfterContainerHook, LifecycleHook.ApplyToChildren {

	@Override
	public void afterContainer(ContainerLifecycleContext context) {
		AfterContainerHookTests.after++;
		assertThat(context.containerClass()).isPresent();
		assertThat(context.containerClass().get())
			.isIn(AfterContainerHookTests.class, AfterContainerHookTests.NestedTests.class);
	}
}

