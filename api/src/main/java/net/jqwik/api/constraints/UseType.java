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
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = MAINTAINED, since = "1.2")
public @interface UseType {
	UseTypeMode[] value() default {};

	/**
	 * When true then type information is also used to generate
	 * embedded types if (and only if) there's no other arbitrary
	 * available for this type.
	 */
	@API(status = MAINTAINED, since = "1.8.0")
	boolean enableRecursion() default true;
}

