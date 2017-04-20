package net.jqwik.api;

import org.junit.platform.commons.annotation.*;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForAll {
	String value() default "";
}
