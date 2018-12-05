package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Allows jqwik to inject null parameters into generated values.
 *
 * Applies to any parameter which is also annotated with {@code @ForAll}.
 *
 * {@code value} specifies the probability between 0 and 1.0 to use for injecting null values.
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithNull {
	double value() default 0.1;
}
