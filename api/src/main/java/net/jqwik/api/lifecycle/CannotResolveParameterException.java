package net.jqwik.api.lifecycle;

import java.lang.reflect.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public class CannotResolveParameterException extends JqwikException {
	public CannotResolveParameterException(ParameterResolutionContext context, String info) {
		this(context.parameter(), info);
	}

	@API(status = EXPERIMENTAL, since = "1.2.5")
	public CannotResolveParameterException(Parameter parameter, String info) {
		super(createMessage(parameter, info));
	}

	private static String createMessage(Parameter parameter, String info) {
		return String.format("Parameter [%s] cannot be resolved:%n\t%s", parameter, info);
	}
}
