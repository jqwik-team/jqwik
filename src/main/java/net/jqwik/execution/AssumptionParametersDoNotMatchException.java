package net.jqwik.execution;

import java.lang.reflect.*;

import net.jqwik.*;

public class AssumptionParametersDoNotMatchException extends JqwikException {

	AssumptionParametersDoNotMatchException(Method propertyMethod, Method assumptionMethod) {
		super(createMessage(propertyMethod, assumptionMethod));
	}

	private static String createMessage(Method propertyMethod, Method assumptionMethod) {
		return String.format("Parameters of assumption method [%s] do not match property method [%s]", assumptionMethod, propertyMethod);
	}

}
