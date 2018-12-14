package net.jqwik.docs.arbitraryconfigurator;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Odd {
}
