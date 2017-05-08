package net.jqwik.properties;

import org.assertj.core.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class ForAllSpy implements CheckedFunction {

	private final Function<Integer, Boolean> returnFunc;
	private final Function<List<Object>, Boolean> argumentsVerifier;
	private final AtomicInteger count = new AtomicInteger(0);

	ForAllSpy(Function<Integer, Boolean> returnFunc, Function<List<Object>, Boolean> argumentsVerifier) {
		this.returnFunc = returnFunc;
		this.argumentsVerifier = argumentsVerifier;
	}

	@Override
	public boolean test(List<Object> args) {
		count.incrementAndGet();
		Assertions.assertThat(argumentsVerifier.apply(args)).isTrue().describedAs("Arguments don't match expectation.");
		return returnFunc.apply(count.get());
	}

	public int countCalls() {
		return count.get();
	}
}
