package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implementations of this class are used to provide single arbitraries for
 * {@code ForAll} parameters and parameter types annotated with {@code From}.
 * Use {@link ForAll#supplier()} or {@link From#supplier()} attributes to specify
 * the actual implementation class.
 *
 * <p>
 *     Either {@link #supplyFor(TypeUsage)} or {@link #get()} must be overridden.
 *     But not both.
 * </p>
 *
 * @see ForAll
 * @see From
 */
@API(status = MAINTAINED, since = "1.7.0")
public interface ArbitrarySupplier<T> extends Supplier<Arbitrary<T>> {

	/**
	 * Override this method if you need more information about the target type,
	 * e.g. annotations or type parameters.
	 *
	 * <p>This method takes precedence if both {@link #supplyFor(TypeUsage)} and {@link #get()} are overridden.</p>
	 *
	 * @param targetType Specifies the type to generate.
	 *
	 * @return A new arbitrary instance
	 */
	@Nullable
	default Arbitrary<T> supplyFor(TypeUsage targetType) {
		return get();
	}

	/**
	 * Override this method if generating the arbitrary is straightforward.
	 * This is probably the standard usage.
	 *
	 * <p>Mind that {@link #supplyFor(TypeUsage)} takes precedence if both
	 * {@link #supplyFor(TypeUsage)} and {@link #get()} are overridden.</p>
	 *
	 * @return A new arbitrary instance
	 */
	@Nullable
	default Arbitrary<T> get() {
		throw new JqwikException("You have to override either ArbitrarySupplier.get() or ArbitrarySupplier.supplyFor()");
	}

	@API(status = INTERNAL, since = "1.6.3")
	class NONE implements ArbitrarySupplier<Void> {}
}
