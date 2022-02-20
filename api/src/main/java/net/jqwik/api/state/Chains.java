package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.7.0")
public class Chains {

	public interface Mutator<T> extends Function<T, T> {}

	@API(status = INTERNAL)
	public static abstract class ChainsFacade {
		private static final Chains.ChainsFacade implementation;

		static {
			implementation = FacadeLoader.load(Chains.ChainsFacade.class);
		}

		public abstract <T> ChainArbitrary<T> chains(
			Supplier<T> initialSupplier,
			Function<Supplier<T>, Arbitrary<Mutator<T>>> chainGenerator
		);
	}

	private Chains() {
	}

	public static <T> ChainArbitrary<T> chains(Supplier<T> initialSupplier, Function<Supplier<T>, Arbitrary<Mutator<T>>> chainGenerator) {
		return ChainsFacade.implementation.chains(initialSupplier, chainGenerator);
	}
}
