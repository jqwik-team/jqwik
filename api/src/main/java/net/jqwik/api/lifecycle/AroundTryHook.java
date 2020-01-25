package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
public interface AroundTryHook extends LifecycleHook<AroundTryHook> {

	boolean aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable;

	AroundTryHook BASE = (tryLifecycleContext, aTry, parameters) -> aTry.execute(parameters);

	@Override
	default int compareTo(AroundTryHook other) {
		return Integer.compare(this.aroundTryProximity(), other.aroundTryProximity());
	}

	default int aroundTryProximity() {
		return 0;
	}

}
