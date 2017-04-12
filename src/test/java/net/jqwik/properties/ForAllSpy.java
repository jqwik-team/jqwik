package net.jqwik.properties;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import org.assertj.core.api.*;

public class ForAllSpy implements Function<List<?>, Boolean> {

	private final Function<Integer, Boolean> returnFunc;
	private final Function<List<?>, Boolean> argumentsVerifier;
	private final AtomicInteger count = new AtomicInteger(0);

	ForAllSpy(Function<Integer, Boolean> returnFunc, Function<List<?>, Boolean> argumentsVerifier) {
		this.returnFunc = returnFunc;
		this.argumentsVerifier = argumentsVerifier;
	}

	@Override
	public Boolean apply(List<?> args) {
		count.incrementAndGet();
		Assertions.assertThat(argumentsVerifier.apply(args)).isTrue().describedAs("Arguments don't match expectation.");
		return returnFunc.apply(count.get());
	}

	public int countCalls() {
		return count.get();
	}
}
