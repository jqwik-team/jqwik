package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the size of generated "sizable" types to be at least 1.
 *
 * Applies to String, List, Set, Stream, and arrays which are also annotated with {@code @ForAll}.
 *
 * Can be combined with {@code @Size(max=XXX)} or {@code @StringLength(max=XXX)}.
 *
 * @see net.jqwik.api.ForAll
 * @see net.jqwik.api.constraints.Size
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Size(min = 1)
@StringLength(min = 1)
@Documented
public @interface NotEmpty {
}
