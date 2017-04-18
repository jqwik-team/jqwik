package net.jqwik.api.properties;

import org.junit.platform.commons.annotation.*;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Property {
	// TODO: Extract default tries to a better place
	int tries() default 1000;
	long seed() default Long.MIN_VALUE;
}
