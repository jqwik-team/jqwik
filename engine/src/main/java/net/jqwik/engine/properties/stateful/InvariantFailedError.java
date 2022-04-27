package net.jqwik.engine.properties.stateful;

class InvariantFailedError extends net.jqwik.engine.properties.state.InvariantFailedError {
	InvariantFailedError(String message, Throwable cause) {
		super(message, cause);
	}
}
