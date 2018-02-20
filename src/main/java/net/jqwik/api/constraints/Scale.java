package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Use to constrain the maximum number of decimal places when generating decimal numbers,
 * i.e. Floats, Doubles and BigDecimals
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scale {
	int value();
}
