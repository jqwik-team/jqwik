package net.jqwik.api.lifecycle;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This is an alias of {@linkplain BeforeProperty}
 *
 * @see AfterExample
 * @see BeforeProperty
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@BeforeProperty
@API(status = MAINTAINED, since = "1.4.0")
public @interface BeforeExample {

}
