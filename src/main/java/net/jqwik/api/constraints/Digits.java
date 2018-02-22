package net.jqwik.api.constraints;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Chars(from = '0', to = '9')
@CharRange(min = '0', max = '9')
@Documented
public @interface Digits {
}
