package net.jqwik.api.lifecycle;

import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This is the supertype of all lifecycle hook interfaces.
 * You can register a concrete implementation of a hook interface using
 * {@linkplain AddLifecycleHook}.
 *
 * @see SkipExecutionHook
 * @see ResolveParameterHook
 * @see RegistrarHook
 * @see BeforeContainerHook
 * @see AfterContainerHook
 * @see AroundPropertyHook
 * @see AroundTryHook
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

	/**
	 * Override this method if you want to change a concrete hook implementation's default
	 * propagation behaviour: Do not use hook in child elements -- sub containers or properties.
	 *
	 * @return propagation mode enum
	 */
	@API(status = EXPERIMENTAL, since = "1.2.4")
	default PropagationMode propagateTo() {
		return PropagationMode.NO_DESCENDANTS;
	}

}
