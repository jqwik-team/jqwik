package net.jqwik.api.arbitraries;

import java.lang.reflect.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that try to generate instances
 * of a given type {@code T} from the type's available constructors and factory methods.
 *
 * <p>
 * When constructors and factory methods have parameters those parameters will be resolved
 * by searching for matching registered arbitrary providers.
 * The searching is performed either globally or in the property method's specified
 * {@linkplain net.jqwik.api.domains.DomainContext domain contexts}.
 * </p>
 *
 * @see net.jqwik.api.domains.Domain
 * @see net.jqwik.api.domains.DomainContext
 */
@API(status = MAINTAINED, since = "1.2")
public interface TypeArbitrary<T extends @Nullable Object> extends Arbitrary<T> {

	/**
	 * Add public constructors of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @return new arbitrary instance
	 */
	TypeArbitrary<T> usePublicConstructors();

	/**
	 * Add all constructors (public, private or package scope) of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @return new arbitrary instance
	 */
	TypeArbitrary<T> useAllConstructors();

	/**
	 * Add all constructors (public, private or package scope) of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @param filter Predicate to add only those constructors for which the predicate returns true
	 * @return new arbitrary instance
	 */
	TypeArbitrary<T> useConstructors(Predicate<? super Constructor<?>> filter);

	/**
	 * Add public factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @return new arbitrary instance
	 */
	TypeArbitrary<T> usePublicFactoryMethods();

	/**
	 * Add all factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @return new arbitrary instance
	 */
	TypeArbitrary<T> useAllFactoryMethods();

	/**
	 * Add all factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @param filter Predicate to add only those factory methods for which the predicate returns true
	 * @return new arbitrary instance
	 */
	TypeArbitrary<T> useFactoryMethods(Predicate<? super Method> filter);

	/**
	 * Enable recursive use of type arbitrary:
	 * If any parameter of a creator function does not have an associated arbitrary
	 * (globally registered or through a domain context),
	 * jqwik will try to resolve this parameter using its type information as weill.
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.8.0")
	TypeArbitrary<T> enableRecursion();
}
