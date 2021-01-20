package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of period values.
 */
@API(status = EXPERIMENTAL, since = "1.4.1")
public interface PeriodArbitrary extends Arbitrary<Period> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated year values.
	 */
	PeriodArbitrary yearsBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	PeriodArbitrary monthsBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day values.
	 */
	PeriodArbitrary daysBetween(int min, int max);

}
