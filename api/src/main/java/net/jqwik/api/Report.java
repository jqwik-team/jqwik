package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @Report} to specify what additional things should be reported
 * when running a property.
 *
 * @see Property
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = MAINTAINED, since = "1.0")
public @interface Report {
	Reporting[] value();
}
