package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the range of a generated float or Float parameters.
 *
 * Applies to float or Float parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see ByteRange
 * @see ShortRange
 * @see IntRange
 * @see LongRange
 * @see DoubleRange
 * @see BigRange
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FloatRange {
	float min() default 0.0f;

	float max();
}
