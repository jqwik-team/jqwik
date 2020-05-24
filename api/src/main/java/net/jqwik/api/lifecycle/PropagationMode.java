package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Describes if and how a concrete registered hook is propagated to children of
 * the element where the hook has been registered. The default behaviour
 * can be changed by:
 *
 * <ul>
 *     <li>Override {@linkplain LifecycleHook#propagateTo()}</li>
 *     <li>Use annotation attribute {@linkplain AddLifecycleHook#propagateTo()}</li>
 * </ul>
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public enum PropagationMode {

	/**
	 * Default of {@linkplain AddLifecycleHook#propagateTo()}. Never use yourself.
	 */
	NOT_SET,

	/**
	 * Propagate to all children and their children
	 */
	ALL_DESCENDANTS,

	/**
	 * Propagate only to direct children
	 */
	DIRECT_DESCENDANTS,

	/**
	 * Do not propagate to any children
	 */
	NO_DESCENDANTS
}
