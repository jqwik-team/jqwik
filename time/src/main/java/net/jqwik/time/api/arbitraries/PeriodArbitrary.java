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

	//TODO: Documentation

	PeriodArbitrary yearsBetween(int min, int max);

	PeriodArbitrary monthsBetween(int min, int max);

	PeriodArbitrary daysBetween(int min, int max);

}
