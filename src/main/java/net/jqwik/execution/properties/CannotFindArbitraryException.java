package net.jqwik.execution.properties;

import net.jqwik.JqwikException;

import java.lang.reflect.Parameter;

public class CannotFindArbitraryException extends JqwikException {

	private final Parameter parameter;

	CannotFindArbitraryException(Parameter parameter) {
		super(String.format("Cannot find an Arbitrary for Parameter [%s]", parameter));
		this.parameter = parameter;
	}

	public Parameter getParameter() {
		return parameter;
	}
}
