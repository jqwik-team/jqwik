package net.jqwik.web.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain generated strings to be valid web domain names (e.g. {@code myhost.com}).
 *
 * <p>
 * Applies to parameters of type {@linkplain String} that are also annotated with {@code @ForAll}.
 * </p>
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.5.5")
public @interface WebDomain {
}
