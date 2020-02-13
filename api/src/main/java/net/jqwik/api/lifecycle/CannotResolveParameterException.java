package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public class CannotResolveParameterException extends JqwikException {
	public CannotResolveParameterException(ParameterResolutionContext context, String info) {
		super(createMessage(context, info));
	}

	private static String createMessage(ParameterResolutionContext context, String info) {
		return String.format("Parameter [%s] without @ForAll cannot be resolved:%n\t%s", context.parameter(), info);
	}
}
