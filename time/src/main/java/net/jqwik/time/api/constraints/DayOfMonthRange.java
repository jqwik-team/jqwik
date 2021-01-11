package net.jqwik.time.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated day of month parameters.
 * <p>
 * Applies to LocalDate parameters which are also annotated with {@code @ForAll}.
 * Also applies to int or Integer parameters which are also annotated with {@code @ForAll} and {@code @DayOfMonth}.
 *
 * @see net.jqwik.api.ForAll
 * @see DayOfMonth
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.1")
public @interface DayOfMonthRange {
	int min() default 1;

	int max() default 31;
}
