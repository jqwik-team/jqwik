package net.jqwik.api.lifecycle;

import java.lang.reflect.*;

import org.apiguardian.api.*;

import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public interface ParameterInjectionContext {

	Parameter parameter();

	TypeUsage usage();
}
