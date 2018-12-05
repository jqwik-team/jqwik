package experiments;

@Retention(RetentionPolicy.RUNTIME)
public @interface Spec {
	String value() default "";
}
