package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The context information of a property or example.
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface PropertyLifecycleContext extends MethodLifecycleContext {

	/**
	 * The extended label contains additional information about the current container class.
	 *
	 * @return a String
	 */
	@API(status = MAINTAINED, since = "1.2.3")
	String extendedLabel();

	/**
	 * An object to query, set or change a property method's attributes.
	 *
	 * @return the attributes holder
	 */
	@API(status = MAINTAINED, since = "1.3.4")
	PropertyAttributes attributes();

}
