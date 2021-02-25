package net.jqwik.time.api.constraints;

import java.lang.annotation.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated hour parameters.
 * <p>
 * Applies to LocalTime and OffsetTime parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see TimeRange
 * @see HourRange
 * @see MinuteRange
 * @see SecondRange
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.5.1")
public @interface Precision {
	ChronoUnit ofPrecision() default SECONDS;
}
