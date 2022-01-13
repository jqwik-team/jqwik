package net.jqwik.time.api.constraints;

import java.lang.annotation.*;
import java.time.format.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated time parameters.
 * Only strings of format {@linkplain DateTimeFormatter#ISO_LOCAL_TIME} are supported.
 * <p>
 * Applies to LocalDateTime, Instant, OffsetDateTime, ZonedDateTime, LocalTime, OffsetTime parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see OffsetRange
 * @see HourRange
 * @see MinuteRange
 * @see SecondRange
 * @see Precision
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.5.1")
public @interface TimeRange {
	String min() default "00:00:00";

	String max() default "23:59:59.0";
}
