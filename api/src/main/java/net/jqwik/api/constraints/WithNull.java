package net.jqwik.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Allows jqwik to inject null parameters into generated values.
 *
 * <p>
 * Applies to any parameter which is also annotated with {@code @ForAll}.
 * <p>
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = MAINTAINED, since = "1.0")
public @interface WithNull {
	/**
	 * @return the probability between 0 and 1.0 to use for injecting null values. Default is 0.05 or 5%.
	 */
	double value() default 0.05;
}
