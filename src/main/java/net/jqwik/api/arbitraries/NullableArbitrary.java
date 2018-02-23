package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

/**
 * Fluent interface to configure arbitraries that can generate null values.
 *
 * While {@code Arbitrary.injectNull()} works with any arbitrary, {@code NullableArbitrary} subtypes
 * can be constraint using {@code getTargetClass()}
 */
public interface NullableArbitrary<T> extends Arbitrary<T> {

	/**
	 * @return The class object of type variable {@code T}.
	 *
	 * Use by annotation {@code @WithNull}
	 *
	 * @see net.jqwik.api.constraints.WithNull
	 */
	Class<?> getTargetClass();

	/**
	 * Set the probability with which null values are generated. Must be between 0.0 and 1.0.
	 */
	NullableArbitrary<T> withNull(double nullProbability);
}
