package net.jqwik.api.properties;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.*;

import javaslang.test.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Property {
	int tries() default Checkable.DEFAULT_TRIES;
}
