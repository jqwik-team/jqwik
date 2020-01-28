package net.jqwik.engine.execution.lifecycle;

import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;

import static net.jqwik.engine.support.JqwikStringSupport.*;

public class ScopedStore<T> implements Store<T> {

	private static final Logger LOG = Logger.getLogger(ScopedStore.class.getName());

	private final Object identifier;
	private final Lifespan lifespan;
	private final TestDescriptor scope;
	private final Supplier<T> initializer;

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
		if (value instanceof AutoCloseable) {
			try {
				((AutoCloseable) value).close();
			} catch (Exception e) {
				String message = String.format("Exception while closing [%s] in store [%s]", value, this);
				LOG.log(Level.SEVERE, message, e);
			}

		}
	}
}

