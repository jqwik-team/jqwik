package net.jqwik.api.properties;

import java.lang.annotation.*;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Assumption {
	String value() default "";
}
