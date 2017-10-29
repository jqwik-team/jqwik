package net.jqwik.api;

import java.lang.annotation.*;

/**
 * Used to annotate methods that can provide values for property method parameters.
 * Those methods must return an instance of <code>Arbitrary<T></code>.
 *
 * <code>value</code> is used as reference name. If it is not specified, the method's name is used instead.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Provide {
	String value() default "";
}
