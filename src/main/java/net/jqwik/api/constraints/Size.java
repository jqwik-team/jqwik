package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Configure the size of generated collection types, i.e. List, Set, Stream, and arrays.
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Size {
	int min() default 0;

	int max();
}
