package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @Disabled("reason to disable")} to disable test container or test method
 * during normal test execution.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = MAINTAINED, since = "1.0")
public @interface Disabled {

	/**
	 * The reason this annotated test container or test method is disabled.
	 */
	String value() default "";

}
