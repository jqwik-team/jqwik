package net.jqwik.engine.properties;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.assertj.core.api.Assertions;

import net.jqwik.api.lifecycle.*;

class ForAllSpy implements TryExecutor {

	private final Function<Integer, Boolean> returnFunc;
	private final Function<List<Object>, Boolean> argumentsVerifier;
	private final AtomicInteger count = new AtomicInteger(0);

	ForAllSpy(Function<Integer, Boolean> returnFunc, Function<List<Object>, Boolean> argumentsVerifier) {
		this.returnFunc = returnFunc;
		this.argumentsVerifier = argumentsVerifier;
	}

	private boolean test(List<Object> parameters) {
		count.incrementAndGet();
		Assertions.assertThat(argumentsVerifier.apply(parameters)).isTrue().describedAs("Arguments don't match expectation.");
		return returnFunc.apply(count.get());
	}

	int countCalls() {
		return count.get();
	}

	@Override
	public TryExecutionResult execute(List<Object> parameters) {
		CheckedFunction checkedFunction = this::test;
		TryExecutor result = checkedFunction.asTryExecutor();
		return result.execute(parameters);
	}
}
