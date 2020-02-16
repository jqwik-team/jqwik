package net.jqwik.api.lifecycle;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface LifecycleContext {

	String label();

	Optional<AnnotatedElement> annotatedElement();

	@API(status = EXPERIMENTAL, since = "1.2.3")
	Reporter reporter();

	@API(status = EXPERIMENTAL, since = "1.2.4")
	<T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass);

	@API(status = EXPERIMENTAL, since = "1.2.4")
	<T> T newInstance(Class<T> clazz);
}
