package experiments;

import java.util.*;

public class GuidanceExperiments {

	interface GenerationGuidance {

		/**
		 * Returns a reference to a source that will deliver
		 * integer values to feed the pseudo-random number generator for the next try.
		 *
		 * @throws IllegalStateException if there is no next try available
		 */
		TryGenerationSource nextTry();

		/**
		 * Decide if another sample can be tried.
		 * <p>
		 * Method could potentially block to wait for guiding algorithm to finish.
		 * <p>
		 * If it returns false generation will be finished.
		 */
		boolean hasNextTry();

		/**
		 * Handles the result of a property try.
		 */
		void handleResult(TryResult result, List<Object> generatedParameters);

	}

	interface TryResult {
		enum Status {
			SATISFIED,
			FALSIFIED,
			INVALID
		}

		Status status();

		Optional<Throwable> throwable();
	}

	/**
	 * Source for providing integer values.
	 */
	interface TryGenerationSource extends AutoCloseable {
		int next();

		boolean hasNext();

		/**
		 * Will be called when no more values are necessary for
		 * generating the parameters of the current try.
		 */
		@Override
		default void close() {
			// Optional
		}
	}
}
