package net.jqwik.time.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated date parameters.
 * <p>
 * Applies to LocalDateTime, LocalDate, Date or Calendar parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see DayOfMonthRange
 * @see DayOfWeekRange
 * @see LeapYears
 * @see MonthDayRange
 * @see MonthRange
 * @see YearMonthRange
 * @see YearRange
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.1")
public @interface DateRange {
	String min() default "1900-01-01";

	String max() default "2500-12-31";
}
