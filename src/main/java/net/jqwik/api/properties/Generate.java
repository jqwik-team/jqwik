package net.jqwik.api.properties;

import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Generate {
	String value() default "";
}
