package net.jqwik.api.lifecycle;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Annotate methods of a container class with {@code @AfterProperty}
 * to have them run once after each property including properties of
 * embedded containers.
 *
 * <p>{@code @AfterProperty} methods are inherited from superclasses
 * and implemented interfaces as long as they are not <em>hidden</em>
 * or <em>overridden</em>.
 *
 * <p>In embedded container classes the {@code @AfterProperty} methods
 * from the inner container are run before the outer container's methods.
 *
 * <p>The execution order of multiple {@code @AfterProperty} methods
 * within the same container is not guaranteed and might change.
 *
 * <p>Parameters of this method will be resolved using registered instances
 * of {@linkplain ResolveParameterHook}. Parameters with annotation
 * {@linkplain net.jqwik.api.ForAll} are not allowed.
 *
 * @see BeforeProperty
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@API(status = EXPERIMENTAL, since = "1.2.4")
public @interface AfterProperty {

}
