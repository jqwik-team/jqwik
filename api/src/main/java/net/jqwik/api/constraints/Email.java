package net.jqwik.api.constraints;

import org.apiguardian.api.API;

import java.lang.annotation.*;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Constrain strings to be valid email addresses
 *
 * Applies to string parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.3.9")
public @interface Email {
	boolean allowDomains() default true;
	boolean allowIPv6() default true;
	boolean allowIPv4() default true;
}
