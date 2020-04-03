package net.jqwik.api.lifecycle;

import java.lang.reflect.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
public interface TryLifecycleContext extends LifecycleContext {

	@API(status = EXPERIMENTAL, since = "1.2.7")
	Method targetMethod();

	@API(status = EXPERIMENTAL, since = "1.2.7")
	Class<?> containerClass();

	@API(status = EXPERIMENTAL, since = "1.2.7")
	Object testInstance();

	@API(status = DEPRECATED, since = "1.2.7")
	@Deprecated
	PropertyLifecycleContext propertyContext();

}
