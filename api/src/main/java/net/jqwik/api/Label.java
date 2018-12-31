package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.annotation.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @Label("a descriptive name")} to give test classes, groups and methods
 * a more readable label (aka display name).
 *
 * @see Property
 * @see Group
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
@API(status = STABLE, since = "1.0")
public @interface Label {
	String value();
}
