package net.jqwik.execution.properties;

import java.lang.reflect.*;

import net.jqwik.*;
import net.jqwik.api.properties.*;

public class CannotFindAssumptionException extends JqwikException {

	CannotFindAssumptionException(Method method) {
		super(createMessage(method));
	}

	private static String createMessage(Method method) {
		String assumeValue = method.getDeclaredAnnotation(Assume.class).value();
		return String.format("Cannot find an Assumption [%s] for Method [%s]", assumeValue, method);
	}

}
