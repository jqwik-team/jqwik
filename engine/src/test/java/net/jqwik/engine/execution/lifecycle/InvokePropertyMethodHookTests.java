package net.jqwik.engine.execution.lifecycle;

import java.lang.reflect.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

@SuppressLogging
class InvokePropertyMethodHookTests {

	static int count = 0;

	@Example
	@AddLifecycleHook(DoNotInvokeReturnNull.class)
	void succeedAssertion() {
		throw new RuntimeException("should not be thrown");
	}

	@Example
	@AddLifecycleHook(DoNotInvokeReturnTrue.class)
	boolean succeedPredicate() {
		throw new RuntimeException("should not be thrown");
	}

	@Property(tries = 10)
	@AddLifecycleHook(IncrementCount.class)
	@PerProperty(AssertCount10.class)
	void callHook10Times() {
	}

	private class AssertCount10 implements PerProperty.Lifecycle {
		@Override
		public void onSuccess() {
			assertThat(count).isEqualTo(10);
		}
	}

	@Example
	@AddLifecycleHook(DoNotInvokeReturnNull.class)
	@AddLifecycleHook(IncrementCount.class)
	void onlyOneHookAllowed() {}

}

class DoNotInvokeReturnNull implements InvokePropertyMethodHook {
	@Override
	public Object invoke(Method method, Object target, Object... args) {
		return null;
	}
}

class DoNotInvokeReturnTrue implements InvokePropertyMethodHook {
	@Override
	public Object invoke(Method method, Object target, Object... args) {
		return true;
	}
}

class IncrementCount implements InvokePropertyMethodHook {
	@Override
	public Object invoke(Method method, Object target, Object... args) throws Throwable {
		InvokePropertyMethodHookTests.count++;
		return InvokePropertyMethodHook.DEFAULT.invoke(method, target, args);
	}
}
