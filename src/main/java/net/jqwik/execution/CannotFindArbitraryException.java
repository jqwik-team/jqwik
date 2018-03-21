package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.support.*;

public class CannotFindArbitraryException extends JqwikException {

	CannotFindArbitraryException(MethodParameter parameter) {
		super(createMessage(parameter));
	}

	public CannotFindArbitraryException(GenericType genericType) {
		super(createMessage("", genericType));
	}

	private static String createMessage(MethodParameter parameter) {
		String forAllValue = parameter.getAnnotation(ForAll.class).value();
		GenericType genericType = GenericType.forParameter(parameter);
		return createMessage(forAllValue, genericType);
	}

	private static String createMessage(String forAllValue, GenericType genericType) {
		if (forAllValue.isEmpty())
			return String.format("Cannot find an Arbitrary for Parameter of type [%s]", genericType);
		else
			return String.format("Cannot find an Arbitrary [%s] for Parameter of type [%s]", forAllValue, genericType);
	}

}
