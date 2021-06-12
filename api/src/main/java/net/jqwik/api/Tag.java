package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @Tag("a tag")} to give test classes, groups and methods an (additional) tag
 * which can later be used to select the set of tests to execute.
 * <p>
 * You can have many tags on the same element.
 * <p>
 * Tags on the JUnit platform must obey a few rules:
 * <ul>
 * <li>A tag must not be blank.</li>
 * <li>A <em>trimmed</em> tag must not contain whitespace.</li>
 * <li>A <em>trimmed</em> tag must not contain ISO control characters.</li>
 * <li>A <em>trimmed</em> tag must not contain any of the following
 * <em>reserved characters</em>.
 * <ul>
 * <li>{@code ,}: <em>comma</em></li>
 * <li>{@code (}: <em>left parenthesis</em></li>
 * <li>{@code )}: <em>right parenthesis</em></li>
 * <li>{@code &}: <em>ampersand</em></li>
 * <li>{@code |}: <em>vertical bar</em></li>
 * <li>{@code !}: <em>exclamation point</em></li>
 * </ul>
 * </li>
 * </ul>
 *
 * <p>
 *     Note that using JUnit Jupiter's annotation {@code org.junit.jupiter.api.Tag}
 *     does not work with jqwik.
 * </p>
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(TagList.class)
@API(status = STABLE, since = "1.0")
public @interface Tag {
	String value();
}
