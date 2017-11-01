package net.jqwik.api.constraints;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CharsList.class)
@Documented
public @interface Chars {
	char from() default 0;

	char to() default 0;

	char[] value() default { };
}
