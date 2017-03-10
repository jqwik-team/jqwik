package net.jqwik;

public class JqwikException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public JqwikException(String message) {
		super(message);
	}

	public JqwikException(String message, Throwable cause) {
		super(message, cause);
	}

}
