package net.jqwik.api.constraints;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithNull {
	double value() default 0.1;
	Class<?> target() default Object.class;
}
