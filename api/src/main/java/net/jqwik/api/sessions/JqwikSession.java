package net.jqwik.api.sessions;

import java.util.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

/**
 * JqwikSession is the abstraction to give users of {@linkplain Arbitrary#sample()}
 * and {@linkplain Arbitrary#sampleStream()} outside the jqwik lifecycle
 * more control over the lifecycle.
 * This has also influence on memory heap usage since an un-finished session will
 * aggregate state, e.g. through caching and other {@linkplain Store stores}.
 */
@API(status = MAINTAINED, since = "1.8.0")
public class JqwikSession {

	@FunctionalInterface
	public interface Runnable {
		void run() throws Throwable;
	}

	@API(status = INTERNAL)
	public abstract static class JqwikSessionFacade {
		private static final JqwikSession.JqwikSessionFacade implementation;

		static {
			implementation = FacadeLoader.load(JqwikSession.JqwikSessionFacade.class);
		}

		public abstract void finishSession();

		public abstract void finishTry();

		public abstract boolean isSessionOpen();

		public abstract void runInSession(@Nullable String randomSeed, Runnable runnable);

		public abstract Optional<Random> getRandom();

		public abstract void startSession(@Nullable String randomSeed);
	}

	public synchronized static void start() {
		JqwikSessionFacade.implementation.startSession(null);
	}

	public static boolean isActive() {
		return JqwikSessionFacade.implementation.isSessionOpen();
	}

	public synchronized static void finish() {
		JqwikSessionFacade.implementation.finishSession();
	}

	public synchronized static void finishTry() {
		JqwikSessionFacade.implementation.finishTry();
	}

	public synchronized static void run(Runnable runnable) {
		JqwikSessionFacade.implementation.runInSession(null, runnable);
	}

	/**
	 * Returns the {@linkplain Random} instance associated with the current session.
	 * @return a Random instance if a session is active, otherwise an empty Optional
	 */
	@API(status = EXPERIMENTAL, since = "1.9.1")
	public static Optional<Random> getRandom() {
		return JqwikSessionFacade.implementation.getRandom();
	}

	/**
	 * Starts a new session with a given random seed.
	 * Currently seeds must be strings that can be parsed by {@linkplain Long#parseLong(String)}.
	 */
	@API(status = EXPERIMENTAL, since = "1.9.1")
	public static void start(String randomSeed) {
		JqwikSessionFacade.implementation.startSession(randomSeed);
	}

	/**
	 * Runs a given {@linkplain Runnable} in a new session with a given random seed.
	 * Currently seeds must be strings that can be parsed by {@linkplain Long#parseLong(String)}.
	 */
	@API(status = EXPERIMENTAL, since = "1.9.1")
	public static void run(String randomSeed, Runnable runnable) {
		JqwikSessionFacade.implementation.runInSession(randomSeed, runnable);
	}

}
