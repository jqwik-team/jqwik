package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Base exception for exceptions that are thrown during the discovery phase
 * and during setup of properties before they are actually run.
 *
 * @see CannotFindArbitraryException
 * @see TooManyFilterMissesException
 */
@API(status = STABLE, since = "1.0")
public class JqwikException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JqwikException(String message) {
		super(message);
	}

	public JqwikException(String message, Throwable cause) {
		super(message, cause);
	}

}
