package experiments;

@Retention(RetentionPolicy.RUNTIME)
public @interface Fixture {
	String referBy() default "";

	Class[] dependsOn() default {};
}
