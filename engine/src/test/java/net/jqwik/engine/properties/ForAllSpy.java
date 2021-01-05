package net.jqwik.engine.properties;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import org.assertj.core.api.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;

class ForAllSpy implements TryLifecycleExecutor {

	private final Function<Integer, Boolean> returnFunc;
	private final Function<List<Object>, Boolean> argumentsVerifier;
	private final AtomicInteger count = new AtomicInteger(0);

	ForAllSpy(Function<Integer, Boolean> returnFunc, Function<List<Object>, Boolean> argumentsVerifier) {
		this.returnFunc = returnFunc;
		this.argumentsVerifier = argumentsVerifier;
	}

	ForAllSpy(Function<Integer, Boolean> returnFunc) {
		this(returnFunc, args -> true);
	}

	private boolean test(List<Object> parameters) {
		count.incrementAndGet();
		Assertions.assertThat(argumentsVerifier.apply(parameters)).describedAs("Arguments don't match expectation.").isTrue();
		return returnFunc.apply(count.get());
	}

	int countCalls() {
		return count.get();
	}

	@Override
	public TryExecutionResult execute(TryLifecycleContext tryLifecycleContext, List<Object> parameters) {
		CheckedFunction checkedFunction = this::test;
		return checkedFunction.execute(parameters);
	}
}
