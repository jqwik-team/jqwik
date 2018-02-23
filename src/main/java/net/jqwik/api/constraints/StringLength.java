package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the length of generated Strings.
 *
 * Applies to String parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StringLength {
	int min() default 0;

	int max();
}
