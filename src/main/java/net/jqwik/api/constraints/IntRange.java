package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the range of a generated int or Integer parameters.
 *
 * Applies to int or Integer parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see ByteRange
 * @see ShortRange
 * @see LongRange
 * @see FloatRange
 * @see DoubleRange
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IntRange {
	int min() default 0;

	int max();
}
