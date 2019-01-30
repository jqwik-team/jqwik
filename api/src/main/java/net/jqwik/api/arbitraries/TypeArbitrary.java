package net.jqwik.api.arbitraries;

import java.lang.reflect.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

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
@API(status = EXPERIMENTAL, since = "1.1")
public interface TypeArbitrary<T> extends Arbitrary<T> {

	/**
	 * Add another creator (function or constructor) to be used
	 * for generating values of type {@code T}
	 *
	 * @param creator The static function or constructor
	 * @return the same arbitrary instance
	 */
	TypeArbitrary<T> use(Executable creator);

	/**
	 * Add public constructors of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @return the same arbitrary instance
	 */
	TypeArbitrary<T> usePublicConstructors();

	/**
	 * Add all constructors (public, private or package scope) of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @return the same arbitrary instance
	 */
	TypeArbitrary<T> useAllConstructors();

	/**
	 * Add all constructors (public, private or package scope) of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @param filter Predicate to add only those constructors for which the predicate returns true
	 * @return the same arbitrary instance
	 */
	TypeArbitrary<T> useConstructors(Predicate<? super Constructor<?>> filter);

	/**
	 * Add public factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @return the same arbitrary instance
	 */
	TypeArbitrary<T> usePublicFactoryMethods();

	/**
	 * Add all factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @return the same arbitrary instance
	 */
	TypeArbitrary<T> useAllFactoryMethods();

	/**
	 * Add all factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @param filter Predicate to add only those factory methods for which the predicate returns true
	 * @return the same arbitrary instance
	 */
	TypeArbitrary<T> useFactoryMethods(Predicate<Method> filter);
}
