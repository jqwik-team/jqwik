package net.jqwik.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Make a generated value to be unique withing the same try.
 *
 * <p>
 * Applies to any embedded types of a parameter that is annotated with {@code @ForAll}.
 * </p>
 *
 * @deprecated Replace with annotation {@linkplain UniqueElements} on container (List, Set, Stream, Iterator and array) parameter.
 *
 * @see net.jqwik.api.ForAll
 * @see net.jqwik.api.constraints.UniqueElements
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
@API(status = DEPRECATED, since = "1.4.0")
public @interface Unique {
}
