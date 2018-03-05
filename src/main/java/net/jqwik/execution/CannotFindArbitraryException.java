package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.lang.reflect.*;

public class CannotFindArbitraryException extends JqwikException {

	private final Parameter parameter;

	CannotFindArbitraryException(Parameter parameter) {
		super(createMessage(parameter));
		this.parameter = parameter;
	}

	private static String createMessage(Parameter parameter) {
		String forAllValue = parameter.getDeclaredAnnotation(ForAll.class).value();
		GenericType genericType = GenericType.forParameter(parameter);
		if (forAllValue.isEmpty())
			return String.format("Cannot find an Arbitrary for Parameter of type [%s]", genericType);
		else
			return String.format("Cannot find an Arbitrary [%s] for Parameter of type [%s]", forAllValue, genericType);
	}

	public Parameter getParameter() {
		return parameter;
	}
}
