package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikStringSupport.*;

public class ScopedStore<T> implements Store<T> {

	private static final Logger LOG = Logger.getLogger(ScopedStore.class.getName());

	private final Object identifier;
	private final Lifespan lifespan;
	private final TestDescriptor scope;
	private final Supplier<T> initializer;

	private final Set<Consumer<T>> onCloseCallbacks = new HashSet<>();
	private T value;
	private boolean initialized = false;

	public ScopedStore(Object identifier, Lifespan lifespan, TestDescriptor scope, Supplier<T> initializer) {
		this.identifier = identifier;
		this.lifespan = lifespan;
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
	public Lifespan lifespan() {
		return lifespan;
	}

	@Override
	public synchronized void update(Function<T, T> updater) {
		value = updater.apply(get());
	}

	@Override
	public synchronized void reset() {
		close();
		initialized = false;
	}

	@Override
	public ScopedStore<T> onClose(Consumer<T> onCloseCallback) {
		onCloseCallbacks.add(onCloseCallback);
		return this;
	}

	public Object getIdentifier() {
		return identifier;
	}

	public TestDescriptor getScope() {
		return scope;
	}

	public boolean isVisibleFor(TestDescriptor retriever) {
		return isInScope(retriever);
	}

	private boolean isInScope(TestDescriptor retriever) {
		if (retriever == scope) {
			return true;
		}
		return retriever.getParent().map(this::isInScope).orElse(false);
	}

	@Override
	public String toString() {
		return String.format(
			"Store(%s, %s, %s): [%s]",
			displayString(identifier),
			lifespan.name(),
			scope.getUniqueId(),
			displayString(value)
		);
	}

	public void close() {
		if (!initialized) {
			return;
		}
		for (Consumer<T> onCloseCallback : onCloseCallbacks) {
			try {
				onCloseCallback.accept(value);
			} catch (Throwable throwable) {
				JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
				String message = String.format("Exception while closing store [%s]", this);
				LOG.log(Level.SEVERE, message, throwable);
			}
		}
	}
}

