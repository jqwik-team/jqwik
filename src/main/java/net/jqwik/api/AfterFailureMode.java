package net.jqwik.api;

/**
 * The after-failure mode determines how a property behaves after it has been falsified.
 * It can be set in {@linkplain Property#afterFailure()} for any property method; default is {@linkplain #PREVIOUS_SEED}.
 *
 * @see Property
 */
public enum AfterFailureMode {

	/**
	 * Use new random seed.
	 */
	RANDOM_SEED,

	/**
	 * Use the previous random seed that already detected a failure.
	 */
	PREVIOUS_SEED,

	/**
	 * Run the property with just the previous falsified and shrunk sample.
	 *
	 * Only works if sample data could be serialized.
	 * Will use previous seed otherwise.
	 */
	SAMPLE_ONLY,

	NOT_SET

}
