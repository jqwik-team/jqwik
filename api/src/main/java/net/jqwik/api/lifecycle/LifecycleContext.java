package net.jqwik.api.lifecycle;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Supertype of all lifecycle context interfaces.
 *
 * @see ContainerLifecycleContext
 * @see PropertyLifecycleContext
 * @see TryLifecycleContext
 */
@API(status = MAINTAINED, since = "1.4.0")
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
	Optional<AnnotatedElement> optionalElement();

	/**
	 * If the context refers to a class or a method the class or the method's class
	 * is returned, otherwise {@code Optional.empty()}
	 *
	 * @return an optional annotated element
	 */
	Optional<Class<?>> optionalContainerClass();

	/**
	 * Get hold of test reporter for publishing additional information on a test container or method.
	 *
	 * @return Current instance to test reporter
	 */
	Reporter reporter();

	/**
	 * Wrap reporter instance
	 *
	 * @param wrapper Wrapping function that takes original reporter and returns a wrapped instance
	 */
	// Not sure this is a good idea because it makes the context object mutable
	@API(status = EXPERIMENTAL, since = "1.5.1")
	void wrapReporter(Function<Reporter, Reporter> wrapper);

	/**
	 * Retrieve an annotation if present at the current test element.
	 *
	 * @param annotationClass The annotation type
	 * @param <T>             The annotation type
	 * @return instance of annotation type
	 */
	<T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass);

	/**
	 * Retrieve an annotation if present in the current element's containers.
	 * Search up the container stack. Closest container annotations come first in list.
	 *
	 * @param annotationClass The annotation type
	 * @param <T>             The annotation type
	 * @return list of annotation objects
	 */
	<T extends Annotation> List<T> findAnnotationsInContainer(Class<T> annotationClass);

	/**
	 * Retrieve a list of repeatable annotations if present at the current test element.
	 *
	 * @param annotationClass The annotation type
	 * @param <T>             The annotation type
	 * @return list of annotation objects
	 */
	@API(status = MAINTAINED, since = "1.7.4")
	<T extends Annotation> List<T> findRepeatableAnnotations(Class<T> annotationClass);

	/**
	 * Create a new instance of a {@code clazz} in the context of the property in which it
	 * is running. Use this method, for example, when trying to instantiate a class
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
	<T> T newInstance(Class<T> clazz);

	/**
	 * Resolve a parameter from a method in the context of the property in which it
	 * is running.
	 *
	 * @param executable The executable of the test container the parameter of which should be resolved
	 * @param index      The parameter's position in a method - starting with 0.
	 * @return supplier instance
	 * @throws CannotResolveParameterException if parameter cannot be resolved
	 */
	Optional<ResolveParameterHook.ParameterSupplier> resolveParameter(Executable executable, int index);
}
