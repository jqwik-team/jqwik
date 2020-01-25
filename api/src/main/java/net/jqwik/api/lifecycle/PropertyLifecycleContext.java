package net.jqwik.api.lifecycle;

import java.lang.reflect.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface PropertyLifecycleContext extends LifecycleContext {

	Method targetMethod();

	Class<?> containerClass();

	Object testInstance();

	@API(status = EXPERIMENTAL, since = "1.2.3")
	String extendedLabel();

}
