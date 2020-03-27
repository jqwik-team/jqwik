package net.jqwik.api.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface LifecycleHook {

	/**
	 * This method is called once per hook and potential element during lifecycle hooks registration.
	 *
	 * @param element The Optional instance contains element for container classes
	 *                or method but is empty for the engine
	 * @return true if a hook shall be applied to this element
	 */
	@API(status = EXPERIMENTAL, since = "1.2.4")
	default boolean appliesTo(Optional<AnnotatedElement> element) {
		return true;
	}

	@API(status = EXPERIMENTAL, since = "1.2.4")
	default PropagationMode propagateTo() {
		return PropagationMode.NO_DESCENDANTS;
	}

}
