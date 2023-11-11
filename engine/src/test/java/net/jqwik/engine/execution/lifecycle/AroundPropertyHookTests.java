package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

@SuppressLogging
class AroundPropertyHookTests {

	static Store<List<String>> calls = Store.create("calls", Lifespan.PROPERTY, ArrayList::new);

	@Example
	@AddLifecycleHook(Outer.class)
	@AddLifecycleHook(Inner.class)
	@AddLifecycleHook(CheckCalls.class)
	void example() {
		calls.update(c -> {
			c.add("example");
			return c;
		});
	}

	static class Outer implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
			calls.update(c -> {
				c.add("before outer");
				return c;
			});
			try {
				return property.execute();
			} finally {
				calls.update(c -> {
					c.add("after outer");
					return c;
				});
			}
		}

		@Override
		public int aroundPropertyProximity() {
			return 10;
		}
	}

	static class Inner implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
			calls.update(c -> {
				c.add("before inner");
				return c;
			});
			try {
				return property.execute();
			} finally {
				calls.update(c -> {
					c.add("after inner");
					return c;
				});
			}
		}

		@Override
		public int aroundPropertyProximity() {
			return 100;
		}
	}

	static class CheckCalls implements AroundPropertyHook {
		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
			try {
				return property.execute();
			} finally {
				assertThat(calls.get()).containsExactly(
					"before outer", "before inner", "example", "after inner", "after outer"
				);
			}
		}
	}

	@Group
	@AddLifecycleHook(AroundPropertyWithPropagation.class)
	@AddLifecycleHook(AroundPropertyWithoutPropagation.class)
	class Propagation {

		@Property(tries = 10)
		void withPropagation() {
			assertThat(AroundPropertyWithPropagation.calls).isEqualTo(2);
		}

		@Property(tries = 10)
		void withoutPropagation() {
			assertThat(AroundPropertyWithoutPropagation.calls).isEqualTo(0);
		}

	}

	static int countSingle = 0;

	@Property(tries = 10)
	@AddLifecycleHook(AroundSingleProperty.class)
	void methodLevelLifecycle() {
		assertThat(countSingle).isEqualTo(1);
	}

	@Group
	class LifecycleContextTests {

		@Property(tries = 2)
		@AddLifecycleHook(CheckPropertyLifecycleContext.class)
		void checkPropertyLifecycleContextAttributes() {
			// All checking is done in the hook
		}
	}
}

class AroundPropertyWithPropagation implements AroundPropertyHook {

	static int calls = 0;

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		calls++;
		return property.execute();
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
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

class CheckPropertyLifecycleContext implements AroundPropertyHook {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		assertThat(context.label()).isEqualTo("checkPropertyLifecycleContextAttributes");
		assertThat(context.extendedLabel()).isEqualTo("LifecycleContextTests:checkPropertyLifecycleContextAttributes");
		assertThat(context.containerClass()).isEqualTo(AroundPropertyHookTests.LifecycleContextTests.class);
		assertThat(context.targetMethod().getName()).isEqualTo("checkPropertyLifecycleContextAttributes");
		assertThat(context.attributes()).isInstanceOf(PropertyAttributes.class);
		assertThat(context.testInstance()).isInstanceOf(AroundPropertyHookTests.LifecycleContextTests.class);

		assertThat(context.testInstances()).hasSize(2);
		assertThat(context.testInstances().get(1)).isEqualTo(context.testInstance());

		List<Class<?>> expectedInstanceClasses = Arrays.asList(
			AroundPropertyHookTests.class,
			AroundPropertyHookTests.LifecycleContextTests.class
		);
		assertThat(context.testInstances()).extracting(Object::getClass).isEqualTo(expectedInstanceClasses);

		return property.execute();
	}
}
