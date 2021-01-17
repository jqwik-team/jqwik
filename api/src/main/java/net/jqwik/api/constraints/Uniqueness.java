package net.jqwik.api.constraints;

import java.lang.annotation.*;
import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Add a uniqueness constraint to a Collection, Stream or Array parameter.
 *
 * Applies to List, Set, Stream, and arrays which are also annotated with {@code @ForAll}.
 *
 * <p>
 *     Unlike the deprecated annotation {@linkplain Unique} this annotation is added to the
 *     collection/array/stream parameter and NOT to the element type.
 * </p>
 *
 * @see net.jqwik.api.ForAll
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = MAINTAINED, since = "1.4.0")
public @interface Uniqueness {
	class NOT_SET implements Function<Object, Object> {
		@Override
		public Object apply(Object o) {
			throw new IllegalArgumentException("This class must not be used");
		}
	}
	Class<? extends Function<Object, Object>> by() default NOT_SET.class;
}
