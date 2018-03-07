package net.jqwik.api;

import org.junit.platform.commons.annotation.*;

import java.lang.annotation.*;

/**
 * Use {@code @Label} to give test classes, groups and methods a more readable label aka display name.
 *
 * @see Property
 * @see Group
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
public @interface Label {
	String value();
}
