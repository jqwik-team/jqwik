package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.state.*;

public class SequentialActionChain<T> implements ActionChain<T> {
	private final Chain<T> chain;

	private volatile T currentValue = null;

	public SequentialActionChain(Chain<T> chain) {
		this.chain = chain;
	}

	@Override
	@NotNull
	public List<String> runActions() {
		return chain.transformations();
	}

	@Override
	@NotNull
	public synchronized T run() {
		for (T state : chain) {
			currentValue = state;
		}
		return currentValue;
	}

	@Override
	@NotNull
	public ActionChain<T> withInvariant(@Nullable String label, Consumer<T> invariant) {
		return null;
	}

	@Override
	@NotNull
	public synchronized Optional<T> finalState() {
		return Optional.ofNullable(currentValue);
	}

	@Override
	@NotNull
	public ActionChain.RunningState running() {
		return null;
	}

	@Override
	@NotNull
	public ActionChain<T> peek(@NotNull Consumer<T> peeker) {
		return null;
	}
}
