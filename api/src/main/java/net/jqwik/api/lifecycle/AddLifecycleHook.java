package net.jqwik.api.lifecycle;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LifecycleHooks.class)
@API(status = EXPERIMENTAL, since = "1.0")
public @interface AddLifecycleHook {

	Class<? extends LifecycleHook> value();

	@API(status = EXPERIMENTAL, since = "1.0")
	PropagationMode propagateTo() default PropagationMode.NOT_SET;
}
