package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.Store.*;

public class ScopedStore<T> {

	private interface Value<T> {
		T get(TestDescriptor retriever);
		void update(TestDescriptor retriever, Function<T, T> updater);
		void reset(TestDescriptor retriever);
	}

	private final Visibility visibility;
	private final TestDescriptor scope;
	private final Supplier<T> initializer;
	private final Value<T> value;

	public ScopedStore(Visibility visibility, TestDescriptor scope, Supplier<T> initializer) {
		this.visibility = visibility;
		this.scope = scope;
		this.initializer = initializer;
		this.value = new LocalValue();
	}

	public synchronized T get(TestDescriptor retriever) {
		return value.get(retriever);
	}

	public synchronized void update(TestDescriptor retriever, Function<T, T> updater) {
		value.update(retriever, updater);
	}

	public synchronized void reset(TestDescriptor retriever) {
		value.reset(retriever);
	}

	public boolean isVisibleFor(TestDescriptor retriever) {
		if (Objects.equals(retriever, scope)) {
			return true;
		}
		return scope.getDescendants().contains(retriever);
	}

	private class LocalValue implements Value<T> {

		private Map<TestDescriptor, T> values = new HashMap<>();
		private Map<TestDescriptor, Boolean> initialized = new HashMap<>();

		@Override
		public T get(TestDescriptor retriever) {
			if (!initialized.getOrDefault(retriever, false)) {
				values.put(retriever, initializer.get());
				initialized.put(retriever, true);
			}
			return values.get(retriever);
		}

		@Override
		public void update(TestDescriptor retriever, Function<T, T> updater) {
			values.put(retriever, updater.apply(get(retriever)));
		}

		@Override
		public void reset(TestDescriptor retriever) {
			values.remove(retriever);
			initialized.remove(retriever);
		}
	}
}

