package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;
import org.opentest4j.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Represents the result of running a property.
 */
@API(status = MAINTAINED, since = "1.4.0")
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

	/**
	 * The seed used to generate randomized parameters.
	 *
	 * @return an optional String
	 */
	Optional<String> seed();

	/**
	 * The potentially shrunk list of parameters that falsified this property.
	 *
	 * <p>
	 * The parameter list returned contains the actual parameters used during the property run.
	 * If one or more parameters were changed during the run, this change is visible here.
	 * </p>
	 *
	 * @return an optional list of parameters
	 */
	Optional<List<Object>> falsifiedParameters();

	/**
	 * The final status of this property
	 *
	 * @return status enum
	 */
	Status status();

	/**
	 * Will return {@code Optional.empty()} if status is anything but FAILED.
	 * If FAILED the optional MUST contain a throwable.
	 */
	Optional<Throwable> throwable();

	/**
	 * The number of tries for which parameters were created
	 * and the property method run.
	 *
	 * @return an number equal to or greater than 0
	 */
	int countChecks();

	/**
	 * The number of tries for which parameters were created and the property method run
	 * and which were not aborted, e.g. through a failing assumption.
	 *
	 * @return an number equal to or greater than 0
	 */
	int countTries();

	/**
	 * Return the original falsified sample if there was one.
	 *
	 * @return an optional falsified sample
	 */
	@API(status = MAINTAINED, since = "1.3.5")
	Optional<FalsifiedSample> originalSample();

	/**
	 * Return the shrunk falsified sample if successful shrinking took place.
	 *
	 * @return an optional falsified sample
	 */
	@API(status = MAINTAINED, since = "1.3.5")
	Optional<ShrunkFalsifiedSample> shrunkSample();

	/**
	 * Use to change the {@linkplain Status status} of a property execution result in a
	 * {@linkplain AroundPropertyHook}.
	 *
	 * @param newStatus Status enum
	 * @param throwable Throwable object or null
	 * @return the changed result object
	 */
	PropertyExecutionResult mapTo(Status newStatus, @Nullable Throwable throwable);

	/**
	 * Use to change the status of a failed property to {@linkplain Status#SUCCESSFUL}
	 * in a {@linkplain AroundPropertyHook}.
	 *
	 * @return the changed result object
	 */
	default PropertyExecutionResult mapToSuccessful() {
		if (status() == Status.SUCCESSFUL) {
			return this;
		}
		return mapTo(Status.SUCCESSFUL, null);
	}

	/**
	 * Use to change the status of a successful property execution to {@linkplain Status#FAILED}
	 * in a {@linkplain AroundPropertyHook}.
	 *
	 * @param throwable Throwable object or null
	 * @return the changed result object
	 */
	default PropertyExecutionResult mapToFailed(@Nullable Throwable throwable) {
		return mapTo(Status.FAILED, throwable);
	}

	/**
	 * Use to change the status of a successful property execution to {@linkplain Status#FAILED}
	 * in a {@linkplain AroundPropertyHook}.
	 *
	 * @param message a String that serves as message of an assertion error
	 * @return the changed result object
	 */
	default PropertyExecutionResult mapToFailed(String message) {
		return mapToFailed(new AssertionFailedError(message));
	}

	/**
	 * Use to change the status of a property execution to {@linkplain Status#ABORTED}
	 * in a {@linkplain AroundPropertyHook}.
	 *
	 * @param throwable Throwable object or null
	 * @return the changed result object
	 */
	default PropertyExecutionResult mapToAborted(@Nullable Throwable throwable) {
		return mapTo(Status.ABORTED, throwable);
	}

}
