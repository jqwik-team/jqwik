package net.jqwik.api.arbitraries;

import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that try to generate instances
 * of a given type {@code T} from the type's available constructors and factory methods.
 */
@API(status = MAINTAINED, since = "1.8.0")
public interface TraverseArbitrary<T> extends Arbitrary<T> {

	/**
	 * A traverser describes how to travers a given type by providing a hook to
	 * resolve a parameter into an arbitrary and a means to find all relevant
	 * creators (constructors or factory methods) for a type that does not have a suitable default arbitrary.
	 */
	interface Traverser {

		/**
		 * Create an arbitrary for a creator parameter.
		 * Only implement if you do not want to resolve the parameter through its default arbitrary (if there is one)
		 *
		 * @param parameterType The typeUsage of the parameter, including annotations
		 * @return New Arbitrary or {@code Optional.empty()}
		 */
		default Optional<Arbitrary<Object>> resolveParameter(TypeUsage parameterType) {
			return Optional.empty();
		}

		/**
		 * Return all creators (constructors or static factory methods) for a type to traverse.
		 *
		 * <p>
		 * If you return an empty set, the attempt to generate a type will be stopped by throwing an exception.
		 * </p>
		 *
		 * @param targetType The target type for which to find creators (factory methods or constructors)
		 * @return A set of at least one creator.
		 */
		Set<Executable> findCreators(TypeUsage targetType);
	}

	/**
	 * Enable recursive use of traversal:
	 * If a parameter of a creator function cannot be resolved,
	 * jqwik will also traverse this parameter's type.
	 *
	 * @return new arbitrary instance
	 */
	TraverseArbitrary<T> enableRecursion();
}
