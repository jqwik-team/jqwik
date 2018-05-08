package net.jqwik.properties.stateful;

import org.opentest4j.*;

//TODO: Make package scope as soon as new shrinking is done
public class InvariantFailedError extends AssertionFailedError {
	public InvariantFailedError(String message, Throwable cause) {
		super(message, cause);
		this.setStackTrace(cause.getStackTrace());
	}
}
