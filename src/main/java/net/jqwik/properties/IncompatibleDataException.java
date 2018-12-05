package net.jqwik.properties;

import net.jqwik.api.*;

public class IncompatibleDataException extends JqwikException {
	public IncompatibleDataException(String message) {
		super(message);
	}
}
