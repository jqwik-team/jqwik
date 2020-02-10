package net.jqwik.api.lifecycle;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface LifecycleHook {

	/**
	 * Marker interface.
	 *
	 * Experimental feature. Not ready for public usage yet.
	 */
	@API(status = EXPERIMENTAL, since = "1.2.1")
	interface PropagateToChildren {
	}

	/**
	 * Experimental feature. Not ready for public usage yet.
	 */
	@API(status = EXPERIMENTAL, since = "1.2.1")
	interface Configurable {
		void configure(Function<String, Optional<String>> parameters);
	}
}
