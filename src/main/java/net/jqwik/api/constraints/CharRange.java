package net.jqwik.api.constraints;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CharRangeList.class)
@Documented
public @interface CharRange {
	char from() default 0;

	char to();
}
