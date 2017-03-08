package experiments;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Fixture {
	String referBy() default "";

	Class[] dependsOn() default {};
}
