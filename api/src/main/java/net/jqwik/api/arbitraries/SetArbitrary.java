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
@API(status = EXPERIMENTAL, since = "1.3.2")
public interface SetArbitrary<T> extends StreamableArbitrary<T, Set<T>> {

	/**
	 * Fix the size to {@code size}.
	 */
	SetArbitrary<T> ofSize(int size);

	/**
	 * Set lower size boundary {@code minSize} (included).
	 */
	SetArbitrary<T> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 */
	SetArbitrary<T> ofMaxSize(int maxSize);

	/**
	 * Map over each element of the generated streamable giving access to the
	 * all elements when streaming.
	 */
	<U> Arbitrary<Set<U>> mapEach(BiFunction<Set<T>, T, U> mapper);

	/**
	 * Flat-map over each element of the generated streamable giving access to the
	 * all elements when streaming.
	 */
	<U> Arbitrary<Set<U>> flatMapEach(BiFunction<Set<T>, T, Arbitrary<U>> flatMapper);
}
