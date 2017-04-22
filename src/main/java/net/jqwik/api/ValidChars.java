package net.jqwik.api;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidChars {
	char from() default 0;
	char to() default 0;
	char[] value() default {};
}
