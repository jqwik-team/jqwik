package net.jqwik.api;

import org.junit.platform.commons.annotation.*;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Property {
	int DEFAULT_TRIES = 1000;
	long DEFAULT_SEED = 0L;

	int tries() default DEFAULT_TRIES;

	long seed() default DEFAULT_SEED;
}
