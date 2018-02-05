package net.jqwik.api;

import org.junit.platform.commons.annotation.*;

import java.lang.annotation.*;

/**
 * Use {@code @Property} to mark methods that serve as properties.
 * Those methods usually have or more parameters annotated with {@linkplain ForAll}.
 *
 * Just like methods annotated with {@linkplain Example} example, annotated methods
 * must not be private. They can either return {@code Boolean}, {@code boolean}
 * or {@code void}.
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Property {
	int TRIES_NOT_SET = 0;
	int MAX_DISCARD_RATIO_NOT_SET = 0;
	long SEED_NOT_SET = 0L;
	String DEFAULT_STEREOTYPE = "Property";

	int tries() default TRIES_NOT_SET;

	int maxDiscardRatio() default MAX_DISCARD_RATIO_NOT_SET;

	long seed() default SEED_NOT_SET;

	ShrinkingMode shrinking() default ShrinkingMode.ON;

	Reporting[] reporting() default {};

	String stereotype() default DEFAULT_STEREOTYPE;
}
