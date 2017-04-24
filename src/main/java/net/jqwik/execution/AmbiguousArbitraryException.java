package net.jqwik.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.properties.*;

public class AmbiguousArbitraryException extends JqwikException {

	AmbiguousArbitraryException(GenericType parameterType, List<Method> creatorMethods) {
		super(createMessage(parameterType, creatorMethods));
	}

	private static String createMessage(GenericType genericType, List<Method> creatorMethods) {
		return String.format("Ambiguous Arbitraries found for Parameter of type [%s]: %s", genericType, creatorMethods);
	}
}
