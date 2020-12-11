package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface TryExecutor {

	TryExecutionResult execute(List<Object> parameters);
}
