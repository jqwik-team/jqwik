package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.state.*;

public class SequentialActionChain<T> implements ActionChain<T> {
	public SequentialActionChain(Chain<T> chain) {}

	@Override
	public List<String> runActions() {
		return null;
	}

	@Override
	public T run() {
		return null;
	}

	@Override
	public ActionChain<T> withInvariant(@Nullable String label, Consumer<T> invariant) {
		return null;
	}

	@Override
	public T finalValue() {
		return null;
	}

	@Override
	public RunState runState() {
		return null;
	}

	@Override
	public ActionChain<T> peek(Consumer<T> peeker) {
		return null;
	}
}
