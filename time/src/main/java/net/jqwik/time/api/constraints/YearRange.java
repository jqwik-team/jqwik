package net.jqwik.time.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated positive year parameters.
 * <p>
 * Applies to LocalDateTime, Instant, OffsetDateTime, LocalDate, Calendar, Date, Year and YearMonth parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see DateRange
 * @see DayOfMonthRange
 * @see DayOfWeekRange
 * @see MonthDayRange
 * @see MonthRange
 * @see YearMonthRange
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.0")
public @interface YearRange {
	int min() default 1900;

	int max() default 2500;
}
