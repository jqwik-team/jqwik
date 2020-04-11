package net.jqwik.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of a generated "Big" number.
 * <p>
 * Applies to {@linkplain java.math.BigInteger} and {@linkplain java.math.BigDecimal} parameters which are also annotated with {@code @ForAll}.
 * <p>
 * {@code minIncluded()} and {@code maxIncluded()} only work for {@linkplain java.math.BigDecimal}.
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
@API(status = MAINTAINED, since = "1.0")
public @interface BigRange {
	String min() default "";

	@API(status = MAINTAINED, since = "1.2.7")
	boolean minIncluded() default true;

	String max() default "";

	@API(status = MAINTAINED, since = "1.2.7")
	boolean maxIncluded() default true;
}
