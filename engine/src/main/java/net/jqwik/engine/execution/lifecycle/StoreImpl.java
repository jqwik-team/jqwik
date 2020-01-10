package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;

public class StoreImpl<T> implements Store<T> {

	private final Visibility visibility;
	private final TestDescriptor owner;
	private final Supplier<T> initializer;
	private boolean initialized = false;
	private T value;

	public StoreImpl(Visibility visibility, TestDescriptor owner, Supplier<T> initializer) {
		this.visibility = visibility;
		this.owner = owner;
		this.initializer = initializer;
	}

	@Override
	public synchronized T get() {
		if (!initialized) {
			value = initializer.get();
			initialized = true;
		}
		return value;
	}

	@Override
	public synchronized void update(Function<T, T> updater) {
		value = updater.apply(get());
	}

	@Override
	public synchronized void reset() {
		initialized = false;
	}

	public boolean isVisibleFor(TestDescriptor retriever) {
		return Objects.equals(retriever, owner);
	}
}
