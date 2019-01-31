package net.jqwik.engine.properties.arbitraries;

public class GenerationError extends RuntimeException {
	public GenerationError(Throwable throwable) {
		super(throwable);
	}
}
