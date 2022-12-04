package net.jqwik.api;

import java.lang.annotation.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Used to annotate methods that can provide values for property method parameters. Those methods must return an
 * instance of {@linkplain Arbitrary}.
 *
 * <p>
 * Methods annotated with {@linkplain Provide} can be present:
 *     <ul>
 *         <li>In test container classes to be picked up by {@linkplain ForAll} parameters.</li>
 *         <li>In subclasses of {@linkplain net.jqwik.api.domains.DomainContextBase} where those methods will be used
 *         to serve as arbitrary providers for a {@linkplain net.jqwik.api.domains.DomainContext}.
 *         </li>
 *     </ul>
 * </p>
 *
 * <p>
 *     Those methods can have optional parameters of type {@linkplain TypeUsage}
 *     or with annotation {@linkplain ForAll}.
 *     The latter will be used to {@linkplain Arbitrary#flatMap(Function) flatMap} over them.
 * </p>
 *
 * @see ForAll
 * @see net.jqwik.api.domains.DomainContextBase
 * @see net.jqwik.api.domains.DomainContext
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = STABLE, since = "1.0")
public @interface Provide {

	/**
	 * Used as reference name. If it is not specified, the method's name is used instead.
	 *
	 * @return A non-empty string if the method should not be referenced by its name
	 */
	String value() default "";

	/**
	 * Used to specify exception types that should be ignored during value generation.
	 *
	 * @return an array of exception types
	 */
	@API(status = EXPERIMENTAL, since = "1.7.2")
	Class<? extends Throwable>[] ignoreExceptions() default {};
}
