package net.jqwik.api;

/**
 * Base exception for exceptions that are thrown during the discovery phase
 * and during setup of properties before they are actually run.
 *
 * @see CannotFindArbitraryException
 * @see TooManyFilterMissesException
 */
public class JqwikException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JqwikException(String message) {
		super(message);
	}

	public JqwikException(String message, Throwable cause) {
		super(message, cause);
	}

}
