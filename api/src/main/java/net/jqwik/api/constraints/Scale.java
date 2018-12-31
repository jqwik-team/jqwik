package net.jqwik.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use to constrain the maximum number of decimal places when generating decimal numbers.
 * If not specified the scale is 2 by default.
 *
 * Applies to Float, Double and BigDecimal parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 */

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = MAINTAINED, since = "1.0")
public @interface Scale {
	int value();
}
