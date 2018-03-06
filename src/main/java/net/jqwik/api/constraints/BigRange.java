package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the range of a generated "Big" number.
 * <p>
 * Applies to BigInteger and BigDecimal parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see ByteRange
 * @see ShortRange
 * @see IntRange
 * @see LongRange
 * @see FloatRange
 * @see DoubleRange
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BigRange {
	String min() default "";

	String max() default "";
}
