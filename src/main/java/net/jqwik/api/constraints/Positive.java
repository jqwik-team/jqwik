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
@ShortRange(min = 0, max = Short.MAX_VALUE)
@ByteRange(min = 0, max = Byte.MAX_VALUE)
@IntRange(min = 0, max = Integer.MAX_VALUE)
@LongRange(min = 0L, max = Long.MAX_VALUE)
@FloatRange(min = 0f, max = Float.MAX_VALUE)
@DoubleRange(min = 0, max = Double.MAX_VALUE)
@BigRange(min = "0")
@Documented
public @interface Positive {
}
