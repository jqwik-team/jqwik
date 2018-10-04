package net.jqwik.api;

import java.lang.annotation.*;

/**
 * Used to annotate methods that can provide data points for property methods.
 * Those methods must return an instance of {@linkplain Iterable}.
 *
 * {@code value} is used as reference name. If it is not specified, the method's name is used instead.
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Data {
	String value() default "";
}
