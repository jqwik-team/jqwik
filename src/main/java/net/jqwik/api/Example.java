package net.jqwik.api;

import net.jqwik.api.properties.*;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Property
public @interface Example {
}
