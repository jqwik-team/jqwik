package net.jqwik.api;

import java.lang.annotation.*;

/**
 * Use {@code @Report} to specify what additional things should be reported
 * when running a property.
 *
 * @see Property
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Report {
	Reporting[] value();
}
