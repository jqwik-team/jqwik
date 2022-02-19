package net.jqwik.api.state;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.FacadeLoader;

import org.apiguardian.api.API;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = EXPERIMENTAL, since = "1.7.0")
public class Chains {

	@API(status = INTERNAL)
	public static abstract class ChainsFacade {
		private static final Chains.ChainsFacade implementation;

		static {
			implementation = FacadeLoader.load(Chains.ChainsFacade.class);
		}

		public abstract  <T> ChainArbitrary<T> chains(Supplier<T> initialSupplier, Function<Supplier<T>, Arbitrary<T>> chainGenerator);
	}

	private Chains() {
	}

	public static <T> ChainArbitrary<T> chains(Supplier<T> initialSupplier, Function<Supplier<T>, Arbitrary<T>> chainGenerator) {
		return ChainsFacade.implementation.chains(initialSupplier, chainGenerator);
	}
}
