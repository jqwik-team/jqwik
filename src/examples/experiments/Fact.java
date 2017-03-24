package experiments;

@Retention(RetentionPolicy.RUNTIME)
public @interface Fact {
	String value() default "";
}
