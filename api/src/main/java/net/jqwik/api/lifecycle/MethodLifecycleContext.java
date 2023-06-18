package net.jqwik.api.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The context information for all method-based lifecyle contexts.
 *
 * @see PropertyLifecycleContext
 * @see TryLifecycleContext
 */
@API(status = EXPERIMENTAL, since = "1.7.4")
public interface MethodLifecycleContext extends LifecycleContext {

	/**
	 * The method that defines the current property or example.
	 *
	 * @return a Method instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	Method targetMethod();

	/**
	 * The container class in which the current property method is running.
	 *
	 * <p>
	 *     Most of the time that's also the defining class. It differs when
	 *     running properties that are defined in a super class or an implemented interface.
	 * </p>
	 *
	 * @return a Class instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	Class<?> containerClass();

	/**
	 * The current instance of the property's container class.
	 * There is exactly one instance per property method.
	 *
	 * @return an instance of the container class in which the current property method is running
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	Object testInstance();

	/**
	 * The list of the current instance of the property's container class and all its outer objects
	 * if it is in a nested class.
	 * The result of {@linkplain #testInstance()} is the last in the list.
	 *
	 * @return List of instances starting from outer-most to inner-most class
	 */
	@API(status = MAINTAINED, since = "1.5.4")
	List<Object> testInstances();

}
