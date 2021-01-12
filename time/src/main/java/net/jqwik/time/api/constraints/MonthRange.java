package net.jqwik.time.api.constraints;

import java.lang.annotation.*;
import java.time.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated month parameters.
 * <p>
 * Applies to YearMonth, MonthDay or LocalDate parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see DateRange
 * @see DayOfMonthRange
 * @see DayOfWeekRange
 * @see MonthDayRange
 * @see YearMonthRange
 * @see YearRange
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.1")
public @interface MonthRange {
	Month min() default Month.JANUARY;

	Month max() default Month.DECEMBER;
}
