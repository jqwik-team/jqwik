package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.support.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Objects of this class represent a property's execution.
 * This is used in {@linkplain AroundPropertyHook}.
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface PropertyExecutor {

	/**
	 * Call to actually run the property, including all hooks that are "closer"
	 * (have a higher proximity) than the current hook.
	 *
	 * @return The execution result
	 */
	PropertyExecutionResult execute();

	/**
	 * {@linkplain #execute()} the property and then call {@code andFinally},
	 * even if property execution fails with an exception
	 *
	 * @param andFinally the code to call after execution
	 * @return The execution result
	 */
	@API(status = EXPERIMENTAL, since = "1.7.4")
	default PropertyExecutionResult executeAndFinally(Runnable andFinally) {
		try {
			PropertyExecutionResult result = this.execute();
			try {
				andFinally.run();
				return result;
			} catch (Throwable throwable) {
				ExceptionSupport.rethrowIfBlacklisted(throwable);
				return result.mapToFailed(throwable);
			}
		} catch (Throwable throwable) {
			ExceptionSupport.rethrowIfBlacklisted(throwable);
			andFinally.run();
			throw throwable;
		}
	}
}
