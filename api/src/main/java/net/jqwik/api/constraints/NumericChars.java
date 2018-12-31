package net.jqwik.api.constraints;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Constrain the chars used to generate Strings or Characters to digits.
 *
 * Applies to String or Character parameters which are also annotated with {@code @ForAll}.
 *
 * When used with String parameters it can be combined with other char constraining annotations.
 * In that case the set of possible characters is expanded by each annotation.
 *
 * @see net.jqwik.api.ForAll
 * @see AlphaChars
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@CharRange(from = '0', to = '9')
@Documented
@API(status = MAINTAINED, since = "1.0")
public @interface NumericChars {
}
