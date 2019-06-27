package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that represent functional types
 */
@API(status = EXPERIMENTAL, since = "1.2.0")
public interface FunctionArbitrary<F> extends Arbitrary<F> {

}
