package net.jqwik.api;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.Testable;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Group {
}
