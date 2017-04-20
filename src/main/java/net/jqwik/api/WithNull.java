package net.jqwik.api;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithNull {
	double value() default 0.1;
	Class<?> target() default Object.class;
}
