package net.jqwik.api.state;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Base class for creating state-based arbitraries for "chains" of values.
 *
 * <p>
 * jqwik has two kinds of chains:
 * </p>
 * <ul>
 *     <li>
 *         Chains based on {@linkplain Transformer transformers}.
 *     	   See {@link #chains(Supplier, TransformerProvider[])} and {@link #chains(Supplier, Tuple2[])}.
 *     </li>
 *     <li>
 *         Chains based on {@linkplain Action actions}.
 *     	   See {@link #actionChains(Supplier, Action[])} (Supplier, Arbitrary[])} and {@link #actionChains(Supplier, Tuple2[])}}.
 *     </li>
 * </ul>
 */
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
			List<Tuple2<Integer, Action<T>>> actionFrequencies
		);
	}

	private Chains() {
	}

	/**
	 * Create arbitrary for a {@linkplain Chain chain} based on {@linkplain Transformer transformers}.
	 *
	 * @param initialSupplier function to create the initial state object
	 * @param providers       varargs of {@linkplain TransformerProvider providers}
	 * @param <T>             The type of state to be transformed through the chain.
	 * @return new arbitrary instance
	 * @see Chain
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> ChainArbitrary<T> chains(Supplier<? extends T> initialSupplier, TransformerProvider<T>... providers) {
		Tuple2<Integer, TransformerProvider<T>>[] frequencies =
			Arrays.stream(providers).map(stepGenerator -> Tuple.of(1, stepGenerator)).toArray(Tuple2[]::new);
		return chains(initialSupplier, frequencies);
	}

	/**
	 * Create arbitrary for a {@linkplain Chain chain} based on {@linkplain Transformer transformers}.
	 *
	 * @param initialSupplier     function to create the initial state object
	 * @param providerFrequencies variable number of Tuples with weight and {@linkplain TransformerProvider provider}.
	 *                            The weight determines the relative probability of a transformer to be chosen.
	 * @param <T>                 The type of state to be transformed through the chain.
	 * @return new arbitrary instance
	 * @see Chain
	 */
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

	/**
	 * Create arbitrary for a {@linkplain ActionChain chain} based on {@linkplain Action actions}.
	 *
	 * @param initialSupplier function to create the initial state object
	 * @param actions         variable number of {@linkplain Action actions}. The actions are randomly chosen with equal probability.
	 * @param <T>             The type of state to be transformed through the chain.
	 * @return new arbitrary instance
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> ActionChainArbitrary<T> actionChains(
		Supplier<? extends T> initialSupplier,
		Action<T>... actions
	) {
		Tuple2<Integer, Action<T>>[] actionFrequencies =
			Arrays.stream(actions).map(a -> Tuple.of(1, a)).toArray(Tuple2[]::new);
		return actionChains(initialSupplier, actionFrequencies);
	}

	/**
	 * Create arbitrary for a {@linkplain ActionChain action chain} based on {@linkplain Action actions}.
	 *
	 * @param initialSupplier   function to create the initial state object
	 * @param actionFrequencies variable number of Tuples with weight and {@linkplain Action action}.
	 *                          The weight determines the relative probability of an action to be chosen.
	 * @param <T>               The type of state to be transformed through the chain.
	 * @return new arbitrary instance
	 */
	@SafeVarargs
	public static <T> ActionChainArbitrary<T> actionChains(
		Supplier<? extends T> initialSupplier,
		Tuple2<Integer, Action<T>>... actionFrequencies
	) {
		List<Tuple2<Integer, Action<T>>> frequencies = Arrays.asList(actionFrequencies);
		if (frequencies.isEmpty()) {
			throw new IllegalArgumentException("You must specify at least one action");
		}
		return ChainsFacade.implementation.actionChains(initialSupplier, frequencies);
	}

}
