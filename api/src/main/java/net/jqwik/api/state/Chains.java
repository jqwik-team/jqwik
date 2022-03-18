package net.jqwik.api.state;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.7.0")
public class Chains {

	@API(status = INTERNAL)
	public static abstract class ChainsFacade {
		private static final Chains.ChainsFacade implementation;

		static {
			implementation = FacadeLoader.load(Chains.ChainsFacade.class);
		}

		public abstract <T> ChainArbitrary<T> chains(
			Supplier<? extends T> initialSupplier,
			List<Tuple2<Integer, TransformerProvider<T>>> providerFrequencies
		);

		public abstract <T> ActionChainArbitrary<T> actionChains(
			Supplier<? extends T> initialSupplier,
			List<Tuple2<Integer,? extends Action<T>>> actionFrequencies
		);
	}

	private Chains() {
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> ChainArbitrary<T> chains(Supplier<? extends T> initialSupplier, TransformerProvider<T>... providers) {
		Tuple2<Integer, TransformerProvider<T>>[] frequencies =
			Arrays.stream(providers).map(stepGenerator -> Tuple.of(1, stepGenerator)).toArray(Tuple2[]::new);
		return chains(initialSupplier, frequencies);
	}

	@SafeVarargs
	public static <T> ChainArbitrary<T> chains(
		Supplier<? extends T> initialSupplier,
		Tuple2<Integer, TransformerProvider<T>>... providerFrequencies
	) {
		List<Tuple2<Integer, TransformerProvider<T>>> generatorsFrequencies = Arrays.asList(providerFrequencies);
		if (generatorsFrequencies.isEmpty()) {
			throw new IllegalArgumentException("You must specify at least one step generator");
		}
		return ChainsFacade.implementation.chains(initialSupplier, generatorsFrequencies);
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T, A extends Action<T>> ActionChainArbitrary<T> actionChains(Supplier<? extends T> initialSupplier, A... actions) {
		Tuple2<Integer, ? extends Action<T>>[] actionFrequencies =
			Arrays.stream(actions).map(a -> Tuple.of(1, a)).toArray(Tuple2[]::new);
		return actionChains(initialSupplier, actionFrequencies);
	}

	@SafeVarargs
	public static <T> ActionChainArbitrary<T> actionChains(
		Supplier<? extends T> initialSupplier,
		Tuple2<Integer, ? extends Action<T>>... actionFrequencies
	) {
		List<Tuple2<Integer, ? extends Action<T>>> frequencies = Arrays.asList(actionFrequencies);
		if (frequencies.isEmpty()) {
			throw new IllegalArgumentException("You must specify at least one action");
		}
		return ChainsFacade.implementation.actionChains(initialSupplier, frequencies);
	}

}
