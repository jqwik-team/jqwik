package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the range of a generated number to be 0 or greater.
 *
 * Applies to numeric parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see Negative
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@ShortRange(min = 0)
@ByteRange(min = 0)
@IntRange(min = 0)
@LongRange(min = 0L)
@FloatRange(min = 0f)
@DoubleRange(min = 0)
@BigRange(min = "0")
@Documented
public @interface Positive {
}
