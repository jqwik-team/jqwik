package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Used to annotate property methods.
 *
 * Only works on methods annotated with {@code @Property}
 *
 * {@code value} is used as reference name to a method annotated with {@code @Data}.
 *
 * @see Property
 * @see Data
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = MAINTAINED, since = "1.0")
public @interface FromData {
	String value();
}
