package net.jqwik.api.properties;

import java.lang.annotation.*;

import javaslang.test.Checkable;
import org.junit.platform.commons.annotation.Testable;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Property {
	int tries() default Checkable.DEFAULT_TRIES;
}
