package net.jqwik.api;

import org.apiguardian.api.*;

import net.jqwik.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;


@API(status = EXPERIMENTAL, since = "1.2.0")
public class Functions {

	@API(status = INTERNAL)
	public static abstract class FunctionsFacade {
		private static Functions.FunctionsFacade implementation;

		static {
			implementation = FacadeLoader.load(Functions.FunctionsFacade.class);
		}

		public abstract void ensureFunctionalType(Class<?> functionalType);

		public abstract <F> FunctionArbitrary<F> function(Class<?> functionalType, Arbitrary<?> resultArbitrary);
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

	public static class FunctionWrapper {
		private Class<?> functionalType;

		private FunctionWrapper(Class<?> functionalType) {
			this.functionalType = functionalType;
		}

		/**
		 * Create an arbitrary to create instances of functions represented by this wrapper.
		 *
		 * The generated functions are guaranteed to return the same result
		 * given the same input values.
		 *
		 * Shrinking will consider constant functions.
		 *
		 * @param resultArbitrary The arbitrary used to generate return values
		 * @param <F> The exact functional type to generate
		 * @return a new arbitrary instance
		 */
		public <F> FunctionArbitrary<F> returns(Arbitrary<?> resultArbitrary) {
			return FunctionsFacade.implementation.function(functionalType, resultArbitrary);
		}
	}
}
