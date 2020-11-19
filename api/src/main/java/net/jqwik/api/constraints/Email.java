package net.jqwik.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain strings to be valid email addresses.
 *
 * Applies to string parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see net.jqwik.api.arbitraries.EmailArbitrary
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.3.9")
public @interface Email {
	boolean domain() default true;
	boolean ipv6Address() default true;
	boolean ipv4Address() default true;
	boolean quotedLocalPart() default true;
	boolean unquotedLocalPart() default true;
}
