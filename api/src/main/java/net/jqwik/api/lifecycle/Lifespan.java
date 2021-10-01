package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Define how long a resource,
 * e.g. the value in a {@linkplain Store} with the same identifier,
 * will live:
 *
 * <ul>
 *     <li>For the whole test run</li>
 *     <li>For the currently running property</li>
 *     <li>For the currently running try</li>
 * </ul>
 *
 * Any hook or collection of hooks can use this enum to allow the specification
 * of the lifespan of resources from which it is abstracting.
 *
 * @see Store
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public enum Lifespan {

	/**
	 * Live for the whole test run
	 */
	RUN,

	/**
	 * Live until the currently running property is finished
	 */
	PROPERTY,

	/**
	 * Live for a single try
	 */
	TRY
}
