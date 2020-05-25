package net.jqwik.api.lifecycle;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Supertype of all lifecycle context interfaces.
 *
 * @see ContainerLifecycleContext
 * @see PropertyLifecycleContext
 * @see TryLifecycleContext
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface LifecycleContext {

	/**
	 * The elements label under which it shows up in test reports and IDEs.
	 *
	 * @return a String
	 */
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

	/**
	 * Get hold of test reporter for publishing additional information on a test container or method.
	 *
	 * @return Current instance to test reporter
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
	Reporter reporter();

	/**
	 * Retrieve an annotation if present at the current test element.
	 *
	 * @param annotationClass The annotation type
	 * @param <T>             The annotation type
	 * @return instance of annotation type
	 */
	@API(status = EXPERIMENTAL, since = "1.2.4")
	<T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass);

	/**
	 * Create a new instance of a {@code clazz} in the context of the property in which it
	 * is running. Use this method for instance when trying to instantiate a class
	 * retrieved from an annotation's attribute.
	 *
	 * <p>
	 * The behaviour of this method differs from {@link Class#newInstance()} if
	 * the class to instantiate is a non-static member of the container class or even
	 * a nested container class.
	 * </p>
	 *
	 * @param clazz The class to instantiate
	 * @param <T> The type to instantiate
	 * @return a freshly created instance of class {@code clazz}
	 */
	@API(status = EXPERIMENTAL, since = "1.2.4")
	<T> T newInstance(Class<T> clazz);

	/**
	 * Resolve a parameter from a method in the context of the property in which it
	 * is running.
	 *
	 * @param executable The executable of the test container the parameter of which should be resolved
	 * @param index      The parameter's position in a method - starting with 0.
	 * @return supplier instance
	 * @throws CannotResolveParameterException
	 */
	@API(status = EXPERIMENTAL, since = "1.2.5")
	Optional<ResolveParameterHook.ParameterSupplier> resolveParameter(Executable executable, int index);
}
