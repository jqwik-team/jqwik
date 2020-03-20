package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@AddLifecycleHook(RegisterCountTries.class)
class RegistrarHookTests {

	static int countTries = 0;

	@BeforeProperty
	void resetCounter() {
		countTries = 0;
	}

	@PerProperty(Counted5.class)
	@Property(tries = 5)
	void property1() {
	}

	class Counted5 implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			Assertions.assertThat(countTries).isEqualTo(5);
		}
	}

	@PerProperty(Counted10.class)
	@Property(tries = 10)
	void property2() {
	}

	class Counted10 implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			Assertions.assertThat(countTries).isEqualTo(10);
		}
	}

	@Group
	class Nested {
		@PerProperty(Counted0.class)
		@Property(tries = 10)
		void registeredHookIsNotPropagatedToNestedTests() {
		}

		class Counted0 implements PerProperty.Lifecycle {
			@Override
			public void onSuccess() {
				Assertions.assertThat(countTries).isEqualTo(0);
			}
		}
	}

}

class RegisterCountTries implements RegistrarHook {

	@Override
	public void registerHooks(Registrar registrar) {
		registrar.register(CountTries.class, PropagationMode.DIRECT_DESCENDANTS);
	}
}

class CountTries implements AroundTryHook {

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		RegistrarHookTests.countTries++;
		return aTry.execute(parameters);
	}
}
