package net.jqwik.api.lifecycle;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Annotate methods of a container class with {@code @AfterTry}
 * to have them run once after each try - the actual invocation of the property
 * method with generated parameters - including properties of
 * embedded containers.
 *
 * <p>{@code @AfterTry} methods are inherited from superclasses
 * and implemented interfaces as long as they are not <em>hidden</em>
 * or <em>overridden</em>.
 *
 * <p>In embedded container classes the {@code @AfterTry} methods
 * from the inner container are run before the outer container's methods.
 *
 * <p>The execution order of multiple {@code @BeforeTry} methods
 * within the same container is not guaranteed and might change.
 *
 * <p>Parameters of this method will be resolved using registered instances
 * of {@linkplain ResolveParameterHook}. Parameters with annotation
 * {@linkplain net.jqwik.api.ForAll} are not allowed.
 *
 * @see BeforeTry
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@API(status = MAINTAINED, since = "1.4.0")
public @interface AfterTry {

}
