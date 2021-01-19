package net.jqwik.time.api.constraints;

import java.lang.annotation.*;
import java.time.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated day of week parameters.
 * <p>
 * Applies to LocalDate, Calendar or Date parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see DateRange
 * @see DayOfMonthRange
 * @see MonthDayRange
 * @see MonthRange
 * @see YearMonthRange
 * @see YearRange
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.1")
public @interface DayOfWeekRange {
	DayOfWeek min() default DayOfWeek.MONDAY;

	DayOfWeek max() default DayOfWeek.SUNDAY;
}
