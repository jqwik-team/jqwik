package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@AddLifecycleHook(RegisterCountTries.class)
@AddLifecycleHook(RegisterRegisterSetTo42.class)
class RegistrarHookTests {

	static int countTries = 0;

	@BeforeProperty
	void resetCounter() {
		countTries = 0;
	}

	@PerProperty(Counted5.class)
	@Property(tries = 5)
	void property1(@ForAll int i) {
	}

	class Counted5 implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			Assertions.assertThat(countTries).isEqualTo(5);
		}
	}

	@PerProperty(Counted10.class)
	@Property(tries = 10)
	void property2(@ForAll int i) {
	}

	class Counted10 implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			Assertions.assertThat(countTries).isEqualTo(10);
		}
	}

	static int setTo42ByNestedRegistrar = 0;

	@Property(tries = 10)
	void propertyWithNestedRegistrar(@ForAll int i) {
		Assertions.assertThat(setTo42ByNestedRegistrar).isEqualTo(42);
		// setTo42ByNestedRegistrar = i;
	}

	@Group
	class Nested {
		@PerProperty(Counted0.class)
		@Property(tries = 10)
		void registeredHookIsNotPropagatedToNestedTests(@ForAll int i) {
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

		// Ignore double registration:
		registrar.register(CountTries.class, PropagationMode.DIRECT_DESCENDANTS);

		// Ignore double registration of registrar hook
		registrar.register(RegisterCountTries.class);
	}
}

class CountTries implements AroundTryHook {

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		RegistrarHookTests.countTries++;
		return aTry.execute(parameters);
	}
}

class RegisterRegisterSetTo42 implements RegistrarHook {

	@Override
	public void registerHooks(Registrar registrar) {
		registrar.register(NestedRegistrar.class);
	}

	static class NestedRegistrar implements RegistrarHook {
		@Override
		public void registerHooks(Registrar registrar) {
			registrar.register(DoingTheWork.class);
		}
	}

	static class DoingTheWork implements BeforeContainerHook {
		@Override
		public void beforeContainer(ContainerLifecycleContext context) {
			RegistrarHookTests.setTo42ByNestedRegistrar = 42;
		}
	}
}