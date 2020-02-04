package experiments;

import java.util.*;

public class GuidanceExperiments {

	interface GenerationGuidance {

		/**
		 * Returns a reference to an iterator that will deliver
		 * return from the pseudo-random number generator.
		 */
		Iterator<Long> guidedStream();

		/**
		 * Decide if more values can be generated.
		 *
		 * Method could potentially block to wait for guiding algorithm to finish.
		 *
		 * If it returns false generation will be finished.
		 */
		boolean continueGeneration();

		/**
		 * Callback for observing actual generated sample passed to the property method.
		 */
		void observeGeneratedSample(List<Object> sample);

		/**
		 * Callback for observing the minimal shrunk sample.
		 */
		void observeShrunkSample(List<Object> sample);

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

		Optional<Throwable> throwable();
	}
}
