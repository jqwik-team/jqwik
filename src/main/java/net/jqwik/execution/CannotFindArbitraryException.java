package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.support.*;

public class CannotFindArbitraryException extends JqwikException {

	CannotFindArbitraryException(MethodParameter parameter) {
		super(createMessage(parameter));
	}

	public CannotFindArbitraryException(TypeUsage typeUsage) {
		super(createMessage("", typeUsage));
	}

	private static String createMessage(MethodParameter parameter) {
		String forAllValue = parameter.getAnnotation(ForAll.class).value();
		TypeUsage typeUsage = TypeUsage.forParameter(parameter);
		return createMessage(forAllValue, typeUsage);
	}

	private static String createMessage(String forAllValue, TypeUsage typeUsage) {
		if (forAllValue.isEmpty())
			return String.format("Cannot find an Arbitrary for Parameter of type [%s]", typeUsage);
		else
			return String.format("Cannot find an Arbitrary [%s] for Parameter of type [%s]", forAllValue, typeUsage);
	}

}
