package net.jqwik.api.arbitraries;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that represent functional types
 *
 * @param <F> The exact functional type to generate
 * @param <R> The return type of the functional interface
 */
@API(status = MAINTAINED, since = "1.3.0")
public interface FunctionArbitrary<F, R extends @Nullable Object> extends Arbitrary<F> {

	/**
	 *
	 * @param <F_> The exact functional type to generate. Must be same as {@code F}
	 *
	 * @param parameterCondition A predicate that's true when for the given list of parameters
	 *                              {@code answer} should be used to produce the function' result
	 * @param answer A function that produces a concrete answer or throws an exception
	 * @return A new instance of function arbitrary
	 */
	<F_ extends F> FunctionArbitrary<F_, R> when(Predicate<? super List<?>> parameterCondition, Function<? super List<?>, ? extends R> answer);

}
