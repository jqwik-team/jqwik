package net.jqwik.time.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the range of generated month and day parameters.
 * <p>
 * Applies to MonthDay parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see DateRange
 * @see DayOfMonthRange
 * @see DayOfWeekRange
 * @see LeapYears
 * @see MonthRange
 * @see YearMonthRange
 * @see YearRange
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.0")
public @interface MonthDayRange {
	String min() default "--01-01";

	String max() default "--12-31";
}
