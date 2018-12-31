package net.jqwik.api.lifecycle;

import java.lang.reflect.Method;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface PropertyLifecycleContext {

	Method targetMethod();

	Class containerClass();

	String label();

	Object testInstance();

}
