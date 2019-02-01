package net.jqwik.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Generate a value from the annotated class' or type's constructors or factory methods.
 *
 * <p>
 * Applies to any parameter that is annotated with {@code @ForAll}.
 * Only the raw type of a parameter is considered.
 * Parameterized and generic types are forbidden.
 * </p>
 *
 * <p>
 * If no {@code value} is given, the default is to use public constructors and
 * factory methods from the annotated type.
 * </p>
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.1")
public @interface UseType {
	UseTypeMode[] value() default {};
}

