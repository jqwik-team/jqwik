package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of period values.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public interface PeriodArbitrary extends Arbitrary<Period> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated period.
	 */
	PeriodArbitrary between(Period min, Period max);

}
