package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.providers.*;

import java.lang.reflect.*;
import java.util.*;

public class AmbiguousArbitraryException extends JqwikException {

	AmbiguousArbitraryException(GenericType parameterType, List<Method> creatorMethods) {
		super(createMessage(parameterType, creatorMethods));
	}

	private static String createMessage(GenericType genericType, List<Method> creatorMethods) {
		return String.format("Ambiguous Arbitraries found for Parameter of type [%s]: %s", genericType, creatorMethods);
	}
}
