package net.jqwik.api.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface ParameterResolutionContext {

	Parameter parameter();

	TypeUsage typeUsage();

	int index();

	@API(status = MAINTAINED, since = "1.5.5")
	Optional<Method> optionalMethod();
}
