package net.jqwik.api.arbitraries;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain Set}
 */
@API(status = MAINTAINED, since = "1.3.2")
public interface SetArbitrary<T> extends StreamableArbitrary<T, Set<T>> {

	/**
	 * Fix the size to {@code size}.
	 *
	 * @param size The size of the generated set
	 * @return new arbitrary instance
	 */
	@Override
	default SetArbitrary<T> ofSize(int size) {
		return this.ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 *
	 * @param minSize The minimum size of the generated set
	 * @return new arbitrary instance
	 */
	SetArbitrary<T> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 *
	 * @param maxSize The maximum size of the generated set
	 * @return new arbitrary instance
	 */
	SetArbitrary<T> ofMaxSize(int maxSize);

	/**
	 * Map over each element of the generated streamable giving access to the
	 * all elements when streaming.
	 *
	 * @param <U> The target type of a set to generate
	 * @param mapper Mapper function to element type U
	 * @return arbitrary of a set of Us
	 */
	@API(status = EXPERIMENTAL, since = "1.2.1")
	<U> Arbitrary<Set<U>> mapEach(BiFunction<Set<T>, T, U> mapper);

	/**
	 * Flat-map over each element of the generated streamable giving access to the
	 * all elements when streaming.
	 *
	 * @param <U> The target type of a set to generate
	 * @param flatMapper Mapper function to arbitrary of element type U
	 * @return arbitrary of a set of Us
	 */
	@API(status = EXPERIMENTAL, since = "1.2.1")
	<U> Arbitrary<Set<U>> flatMapEach(BiFunction<Set<T>, T, Arbitrary<U>> flatMapper);
}
