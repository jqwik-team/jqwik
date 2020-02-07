package net.jqwik.engine.properties.arbitraries;

/**
 * An error during data generation is considered as data to filter out
 */
public class GenerationError extends RuntimeException {
	public GenerationError(Throwable throwable) {
		super(throwable);
	}
}
