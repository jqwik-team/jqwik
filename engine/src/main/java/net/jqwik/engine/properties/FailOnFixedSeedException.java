package net.jqwik.engine.properties;

import net.jqwik.api.*;

public class FailOnFixedSeedException extends JqwikException {
	public FailOnFixedSeedException(String message) {
		super(message);
	}
}
