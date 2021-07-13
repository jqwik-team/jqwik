package net.jqwik.api.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The context information of a property or example.
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface PropertyLifecycleContext extends LifecycleContext {

	/**
	 * The method that defines the current property or example.
	 *
	 * @return a Method instance
	 */
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
	Class<?> containerClass();

	/**
	 * The current instance of the property's container class.
	 * There is exactly one instance per property method.
	 *
	 * @return an instance of the container class in which the current property method is running
	 */
	Object testInstance();

	/**
	 * The list of the current instance of the property's container class and all its outer objects if it has any.
	 * The result of {@linkplain #testInstance()} is the last in the list.
	 *
	 * @return List of instances starting from outer-most to inner-most class
	 */
	@API(status = EXPERIMENTAL, since = "1.5.4")
	List<Object> testInstances();

	/**
	 * The extended label contains additional information about the current container class.
	 *
	 * @return a String
	 */
	@API(status = EXPERIMENTAL, since = "1.2.3")
	String extendedLabel();

	/**
	 * An object to query, set or change a property method's attributes.
	 *
	 * @return the attributes holder
	 */
	@API(status = EXPERIMENTAL, since = "1.3.4")
	PropertyAttributes attributes();

}
