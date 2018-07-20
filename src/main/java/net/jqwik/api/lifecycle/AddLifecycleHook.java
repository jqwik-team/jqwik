package net.jqwik.api.lifecycle;

import java.lang.annotation.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LifecycleHooks.class)
public @interface AddLifecycleHook {

	Class<? extends LifecycleHook> value();
}
