package net.jqwik.api.footnotes;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use this annotation to enable footnote support.
 * Can be applied to container classes and property methods.
 *
 * @see Footnotes
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@AddLifecycleHook(FootnotesHook.class)
@API(status = MAINTAINED, since = "1.5.5")
public @interface EnableFootnotes {

}
