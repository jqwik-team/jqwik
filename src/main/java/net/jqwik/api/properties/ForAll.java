package net.jqwik.api.properties;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface ForAll {
	String value() default "";
	int size() default 0;
}
