package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.annotation.Testable;

import static org.apiguardian.api.API.Status.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
@API(status = STABLE, since = "1.0")
public @interface Group {
}
