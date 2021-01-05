package net.jqwik.api.web;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain strings to be valid email addresses.
 *
 * Applies to string parameters which are also annotated with {@code @ForAll}.
 *
 * @see net.jqwik.api.ForAll
 * @see net.jqwik.api.web.EmailArbitrary
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.0")
public @interface Email {

	/**
	 * Are hosts with domain name allowed.
	 */
	boolean domainHost() default true;

	/**
	 * Are hosts with ipv6 addresses allowed.
	 */
	boolean ipv6Host() default true;

	/**
	 * Are hosts with ipv4 addresses allowed.
	 */
	boolean ipv4Host() default true;

	/**
	 * Are quoted local parts allowed.
	 */
	boolean quotedLocalPart() default true;

	/**
	 * Are unquoted local parts allowed.
	 */
	boolean unquotedLocalPart() default true;
}
