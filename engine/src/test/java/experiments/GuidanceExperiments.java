package experiments;

import java.util.*;

public class GuidanceExperiments {

	interface GenerationGuidance {

		/**
		 * Returns a reference to an iterator that will deliver
		 * integer values to feed the pseudo-random number generator for the next try.
		 *
		 * @throws IllegalStateException if there is no next try available
		 */
		Iterator<Integer> nextTry();

		/**
		 * Decide if another sample can be tried.
		 *
		 * Method could potentially block to wait for guiding algorithm to finish.
		 *
		 * If it returns false generation will be finished.
		 */
		boolean hasNextTry();

		/**
		 * Callback for observing actual generated sample passed to the property method.
		 */
		void observeGeneratedSample(List<Object> sample);

		/**
		 * Handles the result of a property try.
		 */
		void handleResult(TryResult result);

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
}
