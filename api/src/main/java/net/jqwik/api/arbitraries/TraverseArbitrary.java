package net.jqwik.api.arbitraries;

import java.lang.reflect.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that try to generate instances
 * of a given type {@code T} from the type's available constructors and factory methods.
 */
@API(status = EXPERIMENTAL, since = "1.6.1")
public interface TraverseArbitrary<T> extends Arbitrary<T> {

	/**
	 * Add another creator (function or constructor) to be used
	 * for generating values of type {@code T}
	 *
	 * @param creator The static function or constructor
	 * @return new arbitrary instance
	 */
	TraverseArbitrary<T> use(Executable creator);

	/**
	 * Add public constructors of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @return new arbitrary instance
	 */
	TraverseArbitrary<T> usePublicConstructors();

	/**
	 * Add all constructors (public, private or package scope) of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @return new arbitrary instance
	 */
	TraverseArbitrary<T> useAllConstructors();

	/**
	 * Add all constructors (public, private or package scope) of class {@code T} to be used
	 * for generating values of type {@code T}
	 *
	 * @param filter Predicate to add only those constructors for which the predicate returns true
	 * @return the same arbitrary instance
	 */
	TraverseArbitrary<T> useConstructors(Predicate<? super Constructor<?>> filter);

	/**
	 * Add public factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @return new arbitrary instance
	 */
	TraverseArbitrary<T> usePublicFactoryMethods();

	/**
	 * Add all factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @return new arbitrary instance
	 */
	TraverseArbitrary<T> useAllFactoryMethods();

	/**
	 * Add all factory methods (static methods with return type {@code T})
	 * of class {@code T} to be used for generating values of type {@code T}
	 *
	 * @param filter Predicate to add only those factory methods for which the predicate returns true
	 * @return new arbitrary instance
	 */
	TraverseArbitrary<T> useFactoryMethods(Predicate<Method> filter);

	/**
	 * Allow recursive use of traversal:
	 * If a parameter of a creator function cannot be resolved,
	 * jqwik will also traverse this parameter's type.
	 *
	 * @return new arbitrary instance
	 */
	TraverseArbitrary<T> allowRecursion();
}
