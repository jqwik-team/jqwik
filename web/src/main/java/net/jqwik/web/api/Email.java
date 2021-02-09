package net.jqwik.web.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain generated strings to be valid email addresses. By default, only addresses
 * with unquoted local part and domain hosts are generated (e.g. {@code me@myhost.com}),
 * because many - if not most - applications and web forms only accept those.
 *
 * <p>
 * Applies to parameters of type {@linkplain String} that are also annotated with {@code @ForAll}.
 * </p>
 *
 * @see net.jqwik.api.ForAll
 * @see EmailArbitrary
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.4.0")
public @interface Email {

	/**
	 * Are hosts with ipv6 addresses allowed.
	 */
	boolean ipv6Host() default false;

	/**
	 * Are hosts with ipv4 addresses allowed.
	 */
	boolean ipv4Host() default false;

	/**
	 * Are quoted local parts allowed.
	 */
	boolean quotedLocalPart() default false;

}
