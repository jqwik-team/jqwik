package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Make a generated value to be unique withing the same try.
 *
 * <p>
 * Applies to any embedded types of a parameter that is annotated with {@code @ForAll}.
 * </p>
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Unique {
}
