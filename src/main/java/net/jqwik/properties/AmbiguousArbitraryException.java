package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.lang.reflect.*;
import java.util.*;

public class AmbiguousArbitraryException extends JqwikException {

	AmbiguousArbitraryException(TypeUsage parameterType, List<Method> creatorMethods) {
		super(createMessage(parameterType, creatorMethods));
	}

	private static String createMessage(TypeUsage parameterType, List<Method> creatorMethods) {
		return String.format("Ambiguous Arbitraries found for Parameter of type [%s]: %s", parameterType, creatorMethods);
	}
}
