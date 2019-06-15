package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;

public class NotAFunctionalTypeException extends JqwikException {
	public NotAFunctionalTypeException(Class<?> functionClass) {
		super(String.format("%s is not a functional type", functionClass.getName()));
	}
}
