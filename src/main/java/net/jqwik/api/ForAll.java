package net.jqwik.api;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.Testable;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface ForAll {
}
