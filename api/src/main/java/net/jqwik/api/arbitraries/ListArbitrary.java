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
	 */
	ListArbitrary<T> ofSize(int size);

	/**
	 * Set lower size boundary {@code minSize} (included).
	 */
	ListArbitrary<T> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 */
	ListArbitrary<T> ofMaxSize(int maxSize);

	/**
	 * Map over each element of the generated streamable giving access to the
	 * all elements when streaming.
	 */
	<U> Arbitrary<List<U>> mapEach(BiFunction<List<T>, T, U> mapper);

	/**
	 * Flat-map over each element of the generated streamable giving access to the
	 * all elements when streaming.
	 */
	<U> Arbitrary<List<U>> flatMapEach(BiFunction<List<T>, T, Arbitrary<U>> flatMapper);
}
