package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * Constrain the chars used to generate Strings or Characters to chars
 * that will return {@code true} for {@link Character#isWhitespace(char)}.
 *
 * Applies to String or Character parameters which are also annotated with {@code @ForAll}.
 *
 * When used with String parameters it can be combined with other char constraining annotations.
 * In that case the set of possible characters is expanded by each annotation.
 *
 * @see net.jqwik.api.ForAll
 * @see Chars
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Chars({
	'\u0009', //
	'\n', //
	'\u000b', //
	'\u000c', //
	'\r', //
	'\u001c', //
	'\u001d', //
	'\u001e', //
	'\u001f', //
	'\u0020', //
	'\u1680', //
	'\u180e', //
	'\u2000', //
	'\u2001', //
	'\u2002', //
	'\u2003', //
	'\u2004', //
	'\u2005', //
	'\u2006', //
	'\u2008', //
	'\u2009', //
	'\u200a', //
	'\u2028', //
	'\u2029', //
	'\u205f', //
	'\u3000' //
})
@Documented
public @interface Whitespace {
}
