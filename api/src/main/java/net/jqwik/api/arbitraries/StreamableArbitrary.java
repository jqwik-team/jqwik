package net.jqwik.api.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries whose generation artefacts
 * can be streamed, e.g. {@link List}, {@link Set}, {@link Stream} and Arrays
 */
@API(status = MAINTAINED, since = "1.2.1")
public interface StreamableArbitrary<T, U> extends SizableArbitrary<U> {

	/**
	 * Given an {@code initial} argument use {@code accumulator} to produce
	 * the final result.
	 *
	 * @param initial     The initial argument. Also the result if streamable is empty
	 * @param accumulator The function used to reduce a streamable into a result one by one
	 * @param <R>         The result type
	 * @return The result of accumulating all elements in streamable
	 */
	<R> Arbitrary<R> reduce(R initial, BiFunction<R, T, R> accumulator);

	/**
	 * Fix the size to {@code size}.
	 */
	default StreamableArbitrary<T, U> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 */
	StreamableArbitrary<T, U> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 */
	StreamableArbitrary<T, U> ofMaxSize(int maxSize);

	/**
	 * Set distribution {@code distribution} of size of generated arbitrary
	 */
	@API(status = EXPERIMENTAL, since = "1.5.3")
	StreamableArbitrary<T, U> withSizeDistribution(RandomDistribution distribution);
}
