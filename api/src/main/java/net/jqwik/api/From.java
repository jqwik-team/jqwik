package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Used to annotate type parameters within property parameters annotated with {@linkplain ForAll} .
 *
 * {@code value} is used as reference name to a method annotated with {@code @Provide}.
 * If it is not specified, only default providers are considered.
 *
 * @see ForAll
 * @see Provide
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = MAINTAINED, since = "1.3.0")
public @interface From {
	@API(status = INTERNAL, since = "1.6.3")
	String NO_VALUE = "";

	String value() default NO_VALUE;

	@API(status = MAINTAINED, since = "1.8.0")
	Class<? extends ArbitrarySupplier<?>> supplier() default ArbitrarySupplier.NONE.class;
}
