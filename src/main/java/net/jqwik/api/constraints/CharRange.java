package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the chars used to generate Strings or Characters to chars in the range
 * from {@code from} to {@code to}.
 *
 * Applies to String or Character parameters which are also annotated with {@code @ForAll}.
 *
 * When used with String parameters it can be combined with other char constraining annotations.
 * In that case the set of possible characters is expanded by each annotation.
 *
 * @see net.jqwik.api.ForAll
 * @see Chars
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CharRangeList.class)
@Documented
public @interface CharRange {
	char from() default 0;

	char to();
}
