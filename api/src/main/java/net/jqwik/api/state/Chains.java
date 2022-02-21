package net.jqwik.api.state;

import java.util.*;
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
			List<Function<Supplier<T>, Arbitrary<Mutator<T>>>> generatorsList
		);
	}

	private Chains() {
	}

	public static <T> ChainArbitrary<T> chains(Supplier<T> initialSupplier, Function<Supplier<T>, Arbitrary<Mutator<T>>> ... chainGenerators) {
		List<Function<Supplier<T>, Arbitrary<Mutator<T>>>> generatorsList = Arrays.asList(chainGenerators);
		if (generatorsList.isEmpty()) {
			throw new IllegalArgumentException("You must specify at least one chain generator");
		}
		return ChainsFacade.implementation.chains(initialSupplier, generatorsList);
	}
}
