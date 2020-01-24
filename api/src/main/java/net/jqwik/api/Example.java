package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @Example} to mark methods that are simple, example-based
 * test cases. Those methods usually don't have any {@linkplain ForAll} parameters.
 * They are executed only once.
 *
 * Just like methods annotated with {@linkplain Property} example, annotated methods
 * must not be private. They can either return {@code Boolean}, {@code boolean}
 * or {@code void}.
 *
 * @see Property
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Property(tries = 1, shrinking = ShrinkingMode.OFF, stereotype = "Example")
@API(status = STABLE, since = "1.0")
public @interface Example {
}
