package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the range of a generated number to be 0 or less.
 *
 * Applies to numeric parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see Positive
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@ShortRange(min = Short.MIN_VALUE, max = -0)
@ByteRange(min = Byte.MIN_VALUE, max = -0)
@IntRange(min = Integer.MIN_VALUE, max = -0)
@LongRange(min = Long.MIN_VALUE, max = -0L)
@FloatRange(min = -Float.MAX_VALUE, max = -0f)
@DoubleRange(min = -Double.MAX_VALUE, max = -0d)
@BigRange(max = "0")
@Documented
public @interface Negative {
}
