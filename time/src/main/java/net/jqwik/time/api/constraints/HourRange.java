package net.jqwik.time.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated hour parameters.
 * <p>
 * Applies to LocalDateTime, Instant, OffsetDateTime, LocalTime and OffsetTime parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see TimeRange
 * @see OffsetRange
 * @see MinuteRange
 * @see SecondRange
 * @see Precision
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.5.1")
public @interface HourRange {
	int min() default 0;

	int max() default 23;
}
