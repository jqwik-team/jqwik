package net.jqwik.api;

import java.lang.annotation.*;

/**
 * Used to annotate method parameters that will be provided by jqwik.
 *
 * Only works on methods annotated with {@code @Property}
 *
 * {@code value} is used as reference name to a method annotated with {@code @Provide}.
 * If it is not specified, only default providers are considered.
 *
 * @see Property
 * @see Provide
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForAll {
	String value() default "";
}
