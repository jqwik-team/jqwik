package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.state.*;

public class SequentialActionChain<T> implements ActionChain<T> {
	private final Chain<T> chain;

	// TODO: How to initialize with initial state
	private volatile T currentValue = null;

	public SequentialActionChain(Chain<T> chain) {
		this.chain = chain;
	}

	@Override
	public List<String> runActions() {
		return chain.transformations();
	}

	@Override
	public synchronized T run() {
		for (T state : chain) {
			currentValue = state;
		}
		return currentValue;
	}

	@Override
	public ActionChain<T> withInvariant(@Nullable String label, Consumer<T> invariant) {
		return null;
	}

	@Override
	@Nullable
	public synchronized T finalValue() {
		return currentValue;
	}

	@Override
	@NotNull
	public RunState runState() {
		return null;
	}

	@Override
	public ActionChain<T> peek(Consumer<T> peeker) {
		return null;
	}
}
