package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the size of generated "sizable" types.
 *
 * Applies to List, Set, Stream, and arrays which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Size {
	int min() default 0;

	int max();
}
