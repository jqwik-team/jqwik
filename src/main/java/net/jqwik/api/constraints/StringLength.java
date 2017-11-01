package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Configure the length of generated Strings
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StringLength {
	int min() default 0;

	int max();
}
