package net.jqwik.time.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated date time parameters.
 * <p>
 * Applies to Instant parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.5.5")
public @interface InstantRange {
	String min() default "1900-01-01T00:00:00Z";

	String max() default "2500-12-31T23:59:59.0Z";
}
