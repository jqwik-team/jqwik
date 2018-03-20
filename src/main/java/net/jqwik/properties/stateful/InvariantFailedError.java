package net.jqwik.properties.stateful;

class InvariantFailedError extends RuntimeException {
	InvariantFailedError(String message, Throwable cause) {
		super(message, cause);
		this.setStackTrace(cause.getStackTrace());
	}
}
