package net.jqwik.api;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.Testable;

@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Property {
	int DEFAULT_TRIES = 1000;
	int DEFAULT_MAX_DISCARD_RATIO = 5;
	long DEFAULT_SEED = 0L;

	int tries() default DEFAULT_TRIES;

	int maxDiscardRatio() default DEFAULT_MAX_DISCARD_RATIO;

	long seed() default DEFAULT_SEED;

	ShrinkingMode shrinking() default ShrinkingMode.ON;

	ReportingMode reporting() default ReportingMode.MINIMAL;
}
