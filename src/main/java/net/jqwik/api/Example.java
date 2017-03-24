package net.jqwik.api;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Example {
}
