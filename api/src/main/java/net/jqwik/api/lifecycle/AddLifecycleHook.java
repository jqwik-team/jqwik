package net.jqwik.api.lifecycle;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use to register a concrete {@linkplain LifecycleHook lifecycle hook implementation}
 * for a test element -- a container class or a property method. More than one
 * hook can be registered.
 *
 * <p>
 *     This annotation can be used as meta annotation for self-made ones.
 * </p>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LifecycleHooks.class)
@Inherited
@API(status = MAINTAINED, since = "1.4.0")
public @interface AddLifecycleHook {

	/**
	 * @return Concrete lifecycle hook implementation class
	 */
	Class<? extends LifecycleHook> value();

	/**
	 * Override the propagation mode specified in {@linkplain LifecycleHook#propagateTo()}.
	 *
	 * @return Propagation mode enum value
	 */
	PropagationMode propagateTo() default PropagationMode.NOT_SET;
}
