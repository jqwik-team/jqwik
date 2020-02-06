package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface PropertyExecutionResult {

	/**
	 * Status of executing a single test or container.
	 */
	enum Status {

		/**
		 * Indicates that the execution of a property was
		 * <em>successful</em>.
		 */
		SUCCESSFUL,

		/**
		 * Indicates that the execution of a property was
		 * <em>aborted</em> (started but not finished).
		 */
		ABORTED,

		/**
		 * Indicates that the execution of a property has
		 * <em>failed</em>.
		 */
		FAILED
	}

	Optional<String> seed();

	Optional<List<Object>> falsifiedSample();

	Status status();

	/**
	 * Will return {@code Optional.empty()} if status is anything but FAILED.
	 * If FAILED the optional MUST contain a throwable.
	 */
	Optional<Throwable> throwable();

	@API(status = EXPERIMENTAL, since = "1.2.3")
	PropertyExecutionResult changeToSuccessful();

	@API(status = EXPERIMENTAL, since = "1.2.3")
	PropertyExecutionResult changeToFailed(Throwable throwable);

	@API(status = EXPERIMENTAL, since = "1.2.3")
	default PropertyExecutionResult changeToFailed(String message) {
		return changeToFailed(new AssertionFailedError(message));
	}
}
