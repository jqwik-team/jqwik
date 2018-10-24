package net.jqwik.api;

import net.jqwik.*;

public class TooManyFilterMissesException extends JqwikException {
	public TooManyFilterMissesException(String message) {
		super(message);
	}
}
