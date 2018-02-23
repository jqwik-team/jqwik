package net.jqwik.api.constraints;

import java.lang.annotation.*;

/**
 * @deprecated use {@link NumericChars} instead.
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@CharRange(from = '0', to = '9')
@Documented
@Deprecated
public @interface Digits {
}
