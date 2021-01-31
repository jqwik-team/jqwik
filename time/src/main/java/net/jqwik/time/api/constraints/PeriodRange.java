package net.jqwik.time.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated periods.
 * <p>
 * Applies to parameters of type {@linkplain java.time.Period} which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.0")
public @interface PeriodRange {
	String min() default "P-1000Y";

	String max() default "P1000Y";
}
