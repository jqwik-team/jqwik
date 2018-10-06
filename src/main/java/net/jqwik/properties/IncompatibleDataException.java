package net.jqwik.properties;

import net.jqwik.*;

public class IncompatibleDataException extends JqwikException {
	public IncompatibleDataException(String message) {
		super(message);
	}
}
