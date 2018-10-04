package net.jqwik.api;

import java.lang.annotation.*;

/**
 * Used to annotate property methods.
 *
 * Only works on methods annotated with {@code @Property}
 *
 * {@code value} is used as reference name to a method annotated with {@code @Data}.
 *
 * @see Property
 * @see Data
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFrom {
	String value();
}
