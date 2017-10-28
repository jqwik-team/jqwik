package net.jqwik.api;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/**
 * Used to annotate methods that can provide values for property method parameters.
 * Those methods must return an instance of <code>Arbitrary<T></code>.
 *
 * <code>value</code> is used as reference name. If it is not specified, the method's name is used instead.
 */
public @interface Provide {
	String value() default "";
}
