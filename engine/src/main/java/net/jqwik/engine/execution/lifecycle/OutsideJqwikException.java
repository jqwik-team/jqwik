package net.jqwik.engine.execution.lifecycle;

import net.jqwik.api.*;

public class OutsideJqwikException extends JqwikException {
	public OutsideJqwikException(String message) {
		super(message);
	}
}
