package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the range of a generated short or Short parameters.
 *
 * Applies to short or Short parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see ByteRange
 * @see IntRange
 * @see LongRange
 * @see FloatRange
 * @see DoubleRange
 * @see BigRange
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShortRange {
	short min() default 0;

	short max();
}
