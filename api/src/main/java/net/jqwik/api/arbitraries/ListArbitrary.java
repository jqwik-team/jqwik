package net.jqwik.api.arbitraries;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain List}
 */
@API(status = EXPERIMENTAL, since = "1.3.2")
public interface ListArbitrary<T> extends StreamableArbitrary<T, List<T>> {

	/**
	 * Fix the size to {@code size}.
	 *
	 * @param size The size of the generated list
	 * @return new arbitrary instance
	 */
	@Override
	default ListArbitrary<T> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 *
	 * @param minSize The minimum size of the generated list
	 * @return new arbitrary instance
	 */
	ListArbitrary<T> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 *
	 * @param maxSize The maximum size of the generated list
	 * @return new arbitrary instance
	 */
	ListArbitrary<T> ofMaxSize(int maxSize);

	/**
	 * Map over each element of the generated streamable giving access to the
	 * all elements when streaming.
	 *
	 * @param <U> The target type of a list to generate
	 * @param mapper Mapper function to element type U
	 * @return arbitrary of a list of Us
	 */
	<U> Arbitrary<List<U>> mapEach(BiFunction<List<T>, T, U> mapper);

	/**
	 * Flat-map over each element of the generated streamable giving access to the
	 * all elements when streaming.
	 *
	 * @param <U> The target type of a list to generate
	 * @param flatMapper Mapper function to arbitrary of element type U
	 * @return arbitrary of a list of Us
	 */
	<U> Arbitrary<List<U>> flatMapEach(BiFunction<List<T>, T, Arbitrary<U>> flatMapper);
}
