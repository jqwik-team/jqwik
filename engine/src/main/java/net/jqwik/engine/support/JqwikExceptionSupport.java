package net.jqwik.engine.support;

import java.util.*;

public class JqwikExceptionSupport {

	/**
	 * Throw the supplied {@link Throwable}, <em>masked</em> as an
	 * unchecked exception.
	 *
	 * @param t   the Throwable to be wrapped
	 * @param <T> type of the value to return
	 * @return Fake return to make using the method a bit simpler
	 */
	public static <T> T throwAsUncheckedException(Throwable t) {
		JqwikExceptionSupport.throwAs(t);

		// Will never get here
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void throwAs(Throwable t) throws T {
		throw (T) t;
	}

	public static void rethrowIfBlacklisted(Throwable exception) {
		if (exception instanceof OutOfMemoryError) {
			throwAsUncheckedException(exception);
		}
	}

	public static boolean isInstanceOfAny(Throwable throwable, Class<? extends Throwable>[] exceptionTypes) {
		return Arrays.stream(exceptionTypes).anyMatch(exceptionType -> exceptionType.isInstance(throwable));
	}

}
