package net.jqwik.api.lifecycles;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Lifecycle {

	Class<?> value();
}
