package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.annotation.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @Property} to mark methods that serve as properties.
 * Those methods usually have or more parameters annotated with {@linkplain ForAll}.
 *
 * They are executed (tried) several times,
 * either until they fail or until the configured number of {@code tries()} has been reached.
 *
 * Just like methods annotated with {@linkplain Example} example, annotated methods
 * must not be private. They can either return {@code Boolean}, {@code boolean}
 * or {@code void}.
 *
 * @see Example
 * @see ShrinkingMode
 * @see GenerationMode
 * @see AfterFailureMode
 * @see Data
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
@API(status = STABLE, since = "1.0")
public @interface Property {
	int TRIES_NOT_SET = 0;
	int MAX_DISCARD_RATIO_NOT_SET = 0;
	String SEED_NOT_SET = "";
	String DEFAULT_STEREOTYPE = "Property";

	int tries() default TRIES_NOT_SET;

	int maxDiscardRatio() default MAX_DISCARD_RATIO_NOT_SET;

	String seed() default SEED_NOT_SET;

	ShrinkingMode shrinking() default ShrinkingMode.BOUNDED;

	String stereotype() default DEFAULT_STEREOTYPE;

	@API(status = MAINTAINED, since = "1.0")
	GenerationMode generation() default GenerationMode.NOT_SET;

	@API(status = MAINTAINED, since = "1.0")
	AfterFailureMode afterFailure() default AfterFailureMode.NOT_SET;

	@API(status = EXPERIMENTAL, since = "1.3.0")
	EdgeCasesMode edgeCases() default EdgeCasesMode.FIRST_AND_MIXIN;
}
