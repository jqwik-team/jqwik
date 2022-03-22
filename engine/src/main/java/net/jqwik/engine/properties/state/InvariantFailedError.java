package net.jqwik.engine.properties.state;

import org.opentest4j.*;

public class InvariantFailedError extends AssertionFailedError {
	protected InvariantFailedError(String message, Throwable cause) {
		super(message, cause);
		this.setStackTrace(cause.getStackTrace());
	}
}
