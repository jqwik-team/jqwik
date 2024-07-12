package net.jqwik.engine.facades;

import java.util.*;
import java.util.logging.*;

import org.jspecify.annotations.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.*;
import net.jqwik.api.sessions.*;
import net.jqwik.engine.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.engine.TestDescriptor.Type.*;

/**
 * Is loaded through reflection in api module
 */
public class JqwikSessionFacadeImpl extends JqwikSession.JqwikSessionFacade {

	private static final Logger LOG = Logger.getLogger(JqwikSession.class.getName());

	private static final TestDescriptor SESSION_DESCRIPTOR = new AbstractTestDescriptor(
		UniqueId.root("jqwik", "session"),
		"Streaming samples outside jqwik thread"
	) {
		@Override
		public Type getType() {
			return TEST;
		}
	};

	@Override
	public void finishSession() {
		if (!isSessionOpen()) {
			LOG.warning("JqwikSession.finish() should be called within a session");
			return;
		}
		finishSessionLifecycle();
		CurrentTestDescriptor.pop();
	}

	@Override
	public void finishTry() {
		if (!isSessionOpen()) {
			throw new JqwikException("JqwikSession.finishTry() must only be used within a JqwikSession");
		}
		StoreRepository.getCurrent().finishTry(SESSION_DESCRIPTOR);
	}

	@Override
	public boolean isSessionOpen() {
		return !CurrentTestDescriptor.isEmpty() && CurrentTestDescriptor.get() == SESSION_DESCRIPTOR;
	}

	@Override
	public void runInSession(@Nullable String randomSeed, JqwikSession.Runnable runnable) {
		try {
			if (randomSeed != null) {
				SourceOfRandomness.create(randomSeed);
			}
			JqwikSession.start();
			runnable.run();
		} catch (Throwable t) {
			JqwikExceptionSupport.throwAsUncheckedException(t);
		} finally {
			JqwikSession.finish();
		}
	}

	private void finishSessionLifecycle() {
		StoreRepository.getCurrent().finishTry(SESSION_DESCRIPTOR);
		StoreRepository.getCurrent().finishProperty(SESSION_DESCRIPTOR);
		StoreRepository.getCurrent().finishScope(SESSION_DESCRIPTOR);
	}

	@Override
	public Optional<Random> getRandom() {
		if (!isSessionOpen()) {
			return Optional.empty();
		}
		return Optional.of(SourceOfRandomness.current());
	}

	@Override
	public void startSession(@Nullable String randomSeed) {
		if (randomSeed != null) {
			SourceOfRandomness.create(randomSeed);
		}
		startSession();
	}

	private void startSession() {
		if (!CurrentTestDescriptor.isEmpty()) {
			if (CurrentTestDescriptor.get() == SESSION_DESCRIPTOR) {
				throw new JqwikException("JqwikSession.start() cannot be nested");
			} else {
				throw new JqwikException("JqwikSession.start() must only be used outside jqwik's standard lifecycle");
			}
		}
		CurrentTestDescriptor.push(SESSION_DESCRIPTOR);
	}


}
