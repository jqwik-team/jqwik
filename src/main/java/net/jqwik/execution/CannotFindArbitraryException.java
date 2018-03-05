package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.support.*;

public class CannotFindArbitraryException extends JqwikException {

	private final MethodParameter parameter;

	CannotFindArbitraryException(MethodParameter parameter) {
		super(createMessage(parameter));
		this.parameter = parameter;
	}

	private static String createMessage(MethodParameter parameter) {
		String forAllValue = parameter.getDeclaredAnnotation(ForAll.class).value();
		GenericType genericType = GenericType.forParameter(parameter.getNativeParameter());
		if (forAllValue.isEmpty())
			return String.format("Cannot find an Arbitrary for Parameter of type [%s]", genericType);
		else
			return String.format("Cannot find an Arbitrary [%s] for Parameter of type [%s]", forAllValue, genericType);
	}

	public MethodParameter getParameter() {
		return parameter;
	}
}
