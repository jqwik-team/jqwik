package net.jqwik.engine.properties.stateful;

import org.opentest4j.*;

class InvariantFailedError extends AssertionFailedError {
	InvariantFailedError(String message, Throwable cause) {
		super(message, cause);
		this.setStackTrace(cause.getStackTrace());
	}
}
