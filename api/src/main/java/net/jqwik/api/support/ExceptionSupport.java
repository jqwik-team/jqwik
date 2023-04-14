package net.jqwik.api.support;

import org.apiguardian.api.*;

@API(status = API.Status.INTERNAL)
public class ExceptionSupport {

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void throwAs(Throwable t) throws T {
		throw (T) t;
	}

	// TODO: Remove duplication with JqwikExceptionSupport (in engine module)
	public static void rethrowIfBlacklisted(Throwable exception) {
		if (exception instanceof OutOfMemoryError) {
			ExceptionSupport.throwAs(exception);
		}
	}
}
