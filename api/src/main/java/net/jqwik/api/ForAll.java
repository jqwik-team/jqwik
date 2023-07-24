package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Used to annotate method parameters that will be provided by jqwik.
 *
 * Only works on methods annotated with {@code @Property}
 *
 * {@code value} is used as reference name to a method annotated with {@code @Provide}.
 * If it is not specified, only default providers are considered.
 *
 * @see Property
 * @see Provide
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = STABLE, since = "1.0")
public @interface ForAll {
	@API(status = INTERNAL, since = "1.2.0")
	String NO_VALUE = "";

	String value() default NO_VALUE;

	@API(status = MAINTAINED, since = "1.8.0")
	Class<? extends ArbitrarySupplier<?>> supplier() default ArbitrarySupplier.NONE.class;
}
