package net.jqwik.api;

import java.lang.annotation.*;

import org.junit.platform.commons.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Group {
}
