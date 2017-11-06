package net.jqwik.api;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Property(tries = 1)
public @interface Example {
}
