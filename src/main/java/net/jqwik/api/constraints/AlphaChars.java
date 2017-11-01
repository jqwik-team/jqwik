package net.jqwik.api.constraints;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@LowerChars
@UpperChars
@Documented
public @interface AlphaChars {
}
