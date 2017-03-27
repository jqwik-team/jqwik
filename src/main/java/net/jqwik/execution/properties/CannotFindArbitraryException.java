package net.jqwik.execution.properties;

import java.lang.reflect.*;

import net.jqwik.*;
import net.jqwik.api.properties.*;

public class CannotFindArbitraryException extends JqwikException {

	private final Parameter parameter;

	CannotFindArbitraryException(Parameter parameter) {
		super(createMessage(parameter));
		this.parameter = parameter;
	}

	private static String createMessage(Parameter parameter) {
		String forAllValue = parameter.getDeclaredAnnotation(ForAll.class).value();
		GenericType genericType = new GenericType(parameter.getParameterizedType());
		if (forAllValue.isEmpty())
			return String.format("Cannot find an Arbitrary for Parameter of type [%s]", genericType);
		else
			return String.format("Cannot find an Arbitrary [%s] for Parameter of type [%s]", forAllValue, genericType);
	}

	public Parameter getParameter() {
		return parameter;
	}
}
