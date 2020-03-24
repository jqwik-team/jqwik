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

	/**
	 * If the context refers to a class or a method the class or method
	 * is returned, otherwise {@code Optional.empty()}
	 *
	 * @return an optional annotated element
	 */
	@API(status = EXPERIMENTAL, since = "1.2.5")
	Optional<AnnotatedElement> optionalElement();

	/**
	 * If the context refers to a class or a method the class or the method's class
	 * is returned, otherwise {@code Optional.empty()}
	 *
	 * @return an optional annotated element
	 */
	@API(status = EXPERIMENTAL, since = "1.2.5")
	Optional<Class<?>> optionalContainerClass();

	@API(status = EXPERIMENTAL, since = "1.2.3")
	Reporter reporter();

	@API(status = EXPERIMENTAL, since = "1.2.4")
	<T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass);

	@API(status = EXPERIMENTAL, since = "1.2.4")
	<T> T newInstance(Class<T> clazz);

	/**
	 * Resolve a parameter from a method in the context of property in which it
	 * is running.
	 *
	 * @param executable The executable of the test container the parameter of which should be resolved
	 * @param index The parameter's position in a method - starting with 0.
	 * @return supplier instance
	 * @throws CannotResolveParameterException
	 */
	@API(status = EXPERIMENTAL, since = "1.2.5")
	Optional<ResolveParameterHook.ParameterSupplier> resolveParameter(Executable executable, int index);
}
