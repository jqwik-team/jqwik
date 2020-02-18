package net.jqwik.api.lifecycle;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Annotate static methods of a container class with {@code @AfterContainer}
 * to have them run exactly once after all of its properties or any lifecycle
 * methods from embedded containers have been run.
 *
 * <p>{@code @AfterContainer} methods must be {@code static void}
 * and they cannot have parameters.
 *
 * <p>{@code @AfterContainer} methods are inherited from superclasses
 * and implemented interfaces as long as they are not <em>hidden</em>
 * or <em>overridden</em>.
 *
 * <p>The execution order of multiple {@code @AfterContainer} methods
 * within the same container is not guaranteed and might change.
 *
 * @see BeforeContainer
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@API(status = EXPERIMENTAL, since = "1.2.4")
public @interface AfterContainer {

}
