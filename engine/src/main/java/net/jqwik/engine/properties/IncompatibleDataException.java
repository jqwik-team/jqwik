package net.jqwik.engine.properties;

import net.jqwik.api.*;

public class IncompatibleDataException extends JqwikException {
	public IncompatibleDataException(String message) {
		super(message);
	}
}
