package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;

public class ScopedStore<T> implements Store<T> {

	private final Object identifier;
	private final Visibility visibility;
	private final TestDescriptor scope;
	private final Supplier<T> initializer;

	private T value;
	private boolean initialized = false;

	public ScopedStore(Object identifier, Visibility visibility, TestDescriptor scope, Supplier<T> initializer) {
		this.identifier = identifier;
		this.visibility = visibility;
		this.scope = scope;
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

	public Object getIdentifier() {
		return identifier;
	}

	public TestDescriptor getScope() {
		return scope;
	}

	public boolean isVisibleFor(TestDescriptor retriever) {
		switch (visibility) {
			case LOCAL:
				return Objects.equals(retriever, scope);
			default:
				return true;
		}
	}

	@Override
	public String toString() {
		return String.format("Store(%s, %s)", visibility, scope.getUniqueId());
	}
}

