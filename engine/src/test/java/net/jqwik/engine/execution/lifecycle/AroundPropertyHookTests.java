package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;

@AddLifecycleHook(AroundPropertyWithPropagation.class)
@AddLifecycleHook(AroundPropertyWithoutPropagation.class)
class AroundPropertyHookTests {

	@Property(tries = 10)
	void lifeCycleHasBeenCalledAndConfigured1() {
		Assertions.assertThat(AroundPropertyWithPropagation.configured).isEqualTo(1);
		Assertions.assertThat(AroundPropertyWithPropagation.calls).isEqualTo(1);
		Assertions.assertThat(AroundPropertyWithoutPropagation.calls).isEqualTo(0);
	}

	@Property(tries = 10)
	@AddLifecycleHook(AroundSingleProperty.class)
	void  lifeCycleHasBeenCalledAndConfigured2() {
		Assertions.assertThat(AroundPropertyWithPropagation.configured).isEqualTo(1);
		Assertions.assertThat(AroundPropertyWithPropagation.calls).isEqualTo(2);
		Assertions.assertThat(AroundPropertyWithoutPropagation.calls).isEqualTo(0);
		Assertions.assertThat(AroundSingleProperty.calls).isEqualTo(1);
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

	static int calls = 0;

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		calls++;
		return property.execute();
	}
}
