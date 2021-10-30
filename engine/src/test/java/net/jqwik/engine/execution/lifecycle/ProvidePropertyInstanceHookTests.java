package net.jqwik.engine.execution.lifecycle;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

@AddLifecycleHook(UsePredefinedInstance.class)
class ProvidePropertyInstanceHookTests {

	@Example
	void exampleUsesPredefinedInstance() {
		assertThat(this).isSameAs(UsePredefinedInstance.instance);
	}

	@Property(tries = 10)
	void allTriesUsePredefinedInstance(@ForAll int anInt) {
		assertThat(this).isSameAs(UsePredefinedInstance.instance);
	}

	@AfterContainer
	static void hookHasBeenCalledOncePerProperty() {
		assertThat(UsePredefinedInstance.count).isEqualTo(2);
	}
}

class UsePredefinedInstance implements ProvidePropertyInstanceHook {

	static ProvidePropertyInstanceHookTests instance = new ProvidePropertyInstanceHookTests();
	static int count = 0;

	@Override
	public Object provide(Class<?> containerClass) throws Throwable {
		count++;
		return instance;
	}
}
