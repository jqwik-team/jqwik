package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;

class AroundPropertyHookTests {

	@Group
	@AddLifecycleHook(AroundPropertyWithPropagation.class)
	@AddLifecycleHook(AroundPropertyWithoutPropagation.class)
	class Propagation {

		@Property(tries = 10)
		void withPropagation() {
			Assertions.assertThat(AroundPropertyWithPropagation.configured).isEqualTo(1);
			Assertions.assertThat(AroundPropertyWithPropagation.calls).isEqualTo(2);
		}

		@Property(tries = 10)
		void withoutPropagation() {
			Assertions.assertThat(AroundPropertyWithoutPropagation.calls).isEqualTo(0);
		}

	}

	static int countSingle = 0;

	@Property(tries = 10)
	@AddLifecycleHook(AroundSingleProperty.class)
	void methodLevelLifecycle() {
		Assertions.assertThat(countSingle).isEqualTo(1);
	}

}

class AroundPropertyWithPropagation implements AroundPropertyHook, Configurable, PropagateToChildren {

	static int calls = 0;
	static int configured = 0;

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		calls++;
		return property.execute();
	}

	@Override
	public void configure(Function<String, Optional<String>> parameters) {
		configured++;
	}
}

class AroundPropertyWithoutPropagation implements AroundPropertyHook {

	static int calls = 0;

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		calls++;
		return property.execute();
	}
}

class AroundSingleProperty implements AroundPropertyHook {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		AroundPropertyHookTests.countSingle++;
		return property.execute();
	}
}
