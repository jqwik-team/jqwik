package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the range of a generated long or BigInteger parameters.
 *
 * Applies to long or Long parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see ByteRange
 * @see ShortRange
 * @see IntRange
 * @see FloatRange
 * @see DoubleRange
 * @see BigRange
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LongRange {
	long min() default 0;

	long max() default Long.MAX_VALUE;
}
