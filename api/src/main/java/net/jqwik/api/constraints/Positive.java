package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the range of a generated number to be greater than 0.
 *
 * Applies to numeric parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see Negative
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Positive {
}
