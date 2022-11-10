package net.jqwik.docs.arbitraryconfigurator;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
public @interface NotNull {
	/* Empty on purpose */
}
