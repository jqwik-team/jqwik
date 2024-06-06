package net.jqwik.api.arbitraries;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type {@linkplain Map}
 */
@API(status = MAINTAINED, since = "1.3.2")
public interface MapArbitrary<K extends @Nullable Object, V extends @Nullable Object> extends Arbitrary<Map<K, V>>, SizableArbitrary<Map<K, V>> {

	/**
	 * Fix the size to {@code size}.
	 *
	 * @param size The size of the generated map
	 * @return new arbitrary instance
	 */
	@Override
	default MapArbitrary<K, V> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

	/**
	 * Set lower size boundary {@code minSize} (included).
	 *
	 * @param minSize The minimum size of the generated map
	 * @return new arbitrary instance
	 */
	MapArbitrary<K, V> ofMinSize(int minSize);

	/**
	 * Set upper size boundary {@code maxSize} (included).
	 *
	 * @param maxSize The maximum size of the generated map
	 * @return new arbitrary instance
	 */
	MapArbitrary<K, V> ofMaxSize(int maxSize);

	/**
	 * Set random distribution {@code distribution} of size of generated map.
	 * The distribution's center is the minimum size of the generated map.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.3")
	MapArbitrary<K, V> withSizeDistribution(RandomDistribution uniform);

	/**
	 * Add the constraint that keys of the generated map must be unique
	 * relating to an element's "feature" being extracted by applying the
	 * {@code by} function on a map entry's key.
	 * The extracted features are being compared using {@linkplain Object#equals(Object)}.
	 *
	 * <p>
	 *     The constraint can be combined with other {@linkplain #uniqueKeys(Function)} constraints.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	MapArbitrary<K, V> uniqueKeys(Function<K, Object> by);

	/**
	 * Add the constraint that value of the generated map must be unique
	 * relating to an element's "feature" being extracted by applying the
	 * {@code by} function on a map entry's value.
	 * The extracted features are being compared using {@linkplain Object#equals(Object)}.
	 *
	 * <p>
	 *     The constraint can be combined with other {@linkplain #uniqueValues(Function)} constraints.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	MapArbitrary<K, V> uniqueValues(Function<V, Object> by);

	/**
	 * Add the constraint that values of the generated map must be unique,
	 * i.e. no two value must return true when being compared using {@linkplain Object#equals(Object)}.
	 *
	 * <p>
	 *     The constraint can be combined with other {@linkplain #uniqueValues(Function)} constraints.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	MapArbitrary<K, V> uniqueValues();

}
