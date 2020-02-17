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
		 * <em>aborted</em> before the actual property method could be run.
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

	@API(status = EXPERIMENTAL, since = "1.2.4")
	int countChecks();

	@API(status = EXPERIMENTAL, since = "1.2.4")
	int countTries();

	@API(status = EXPERIMENTAL, since = "1.2.4")
	PropertyExecutionResult mapTo(Status newStatus, Throwable throwable);

	@API(status = EXPERIMENTAL, since = "1.2.4")
	default PropertyExecutionResult mapToSuccessful() {
		if (status() == Status.SUCCESSFUL) {
			return this;
		}
		return mapTo(Status.SUCCESSFUL, null);
	}

	@API(status = EXPERIMENTAL, since = "1.2.4")
	default PropertyExecutionResult mapToFailed(Throwable throwable) {
		return mapTo(Status.FAILED, throwable);
	}

	@API(status = EXPERIMENTAL, since = "1.2.4")
	default PropertyExecutionResult mapToFailed(String message) {
		return mapToFailed(new AssertionFailedError(message));
	}

	@API(status = EXPERIMENTAL, since = "1.2.4")
	default PropertyExecutionResult mapToAborted(Throwable throwable) {
		return mapTo(Status.ABORTED, throwable);
	}

}
