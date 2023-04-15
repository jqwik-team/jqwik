package net.jqwik.api.sessions;

import org.apiguardian.api.*;

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
@API(status = EXPERIMENTAL, since = "1.6.0")
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

		public abstract void startSession();

		public abstract void finishSession();

		public abstract void finishTry();

		public abstract boolean isSessionOpen();

		public abstract void runInSession(Runnable runnable);
	}

	public synchronized static void start() {
		JqwikSessionFacade.implementation.startSession();
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
		JqwikSessionFacade.implementation.runInSession(runnable);
	}


}
