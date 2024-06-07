package net.jqwik.api;

import org.apiguardian.api.*;

import net.jqwik.api.arbitraries.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;


@API(status = MAINTAINED, since = "1.3.0")
public class Functions {

	@API(status = INTERNAL)
	public static abstract class FunctionsFacade {
		private static final Functions.FunctionsFacade implementation;

		static {
			implementation = FacadeLoader.load(Functions.FunctionsFacade.class);
		}

		public abstract void ensureFunctionalType(Class<?> functionalType);

		public abstract <F, R extends @Nullable Object> FunctionArbitrary<F, R> function(Class<?> functionalType, Arbitrary<R> resultArbitrary);

	}


	private Functions() {
	}

	/**
	 * Create a wrapper for functional types
	 * i.e. types marked {@linkplain FunctionalInterface} or representing a
	 * SAM (single abstract method) type.
	 *
	 * @param functionalType The class object of the functional type to generate
	 * @return a new function wrapper instance
	 * @throws JqwikException if {@code functionalType} is not a functional type
	 */
	public static FunctionWrapper function(Class<?> functionalType) {
		FunctionsFacade.implementation.ensureFunctionalType(functionalType);
		return new FunctionWrapper(functionalType);
	}

	/**
	 * Wraps a function to be generated.
	 */
	public static class FunctionWrapper {
		private final Class<?> functionalType;

		private FunctionWrapper(Class<?> functionalType) {
			this.functionalType = functionalType;
		}

		/**
		 * Create an arbitrary to create instances of functions represented by this wrapper.
		 * The generated functions are guaranteed to return the same result
		 * given the same input values.
		 *
		 * Shrinking will consider constant functions.
		 *
		 * @param resultArbitrary The arbitrary used to generate return values
		 * @param <F> The exact functional type to generate
		 * @param <R> The return type of the functional interface
		 * @return a new arbitrary instance
		 */
		@API(status = MAINTAINED, since = "1.6.0")
		public <F, R extends @Nullable Object> FunctionArbitrary<F, R> returning(Arbitrary<R> resultArbitrary) {
			return FunctionsFacade.implementation.function(functionalType, resultArbitrary);
		}
	}
}
