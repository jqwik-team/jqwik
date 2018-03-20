package net.jqwik.properties.stateful;

class InvariantFailedError extends RuntimeException {
	InvariantFailedError(String message) {
		super(message);
	}

	InvariantFailedError(String message, Throwable t) {
		super(message, t);
	}
}
