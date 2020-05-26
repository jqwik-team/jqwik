package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Objects of this class represent a property's execution.
 * This is used in {@linkplain AroundPropertyHook}.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface PropertyExecutor {

	/**
	 * Call to actually run the property, including all hooks that are "closer"
	 * (have a higher proximity) than the current hook.
	 *
	 * @return The execution result
	 */
	PropertyExecutionResult execute();
}
