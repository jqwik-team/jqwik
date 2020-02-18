package net.jqwik.api.lifecycle;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Annotate methods of a container class with {@linkplain @BeforeTry}
 * to have them run once before each try - the actual invocation of the property
 * method with generated parameters - including properties of
 * embedded containers.
 *
 * <p>{@code @BeforeTry} methods are inherited from superclasses
 * and implemented interfaces as long as they are not <em>hidden</em>
 * or <em>overridden</em>.
 *
 * <p>The execution order of multiple {@code @BeforeTry} methods
 * within the same container is not guaranteed and might change.
 *
 * @see AfterTry
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@API(status = EXPERIMENTAL, since = "1.2.4")
public @interface BeforeTry {

}
