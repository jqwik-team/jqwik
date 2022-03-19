package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.state.*;

public class DefaultActionChainArbitrary<T> extends ArbitraryDecorator<ActionChain<T>> implements ActionChainArbitrary<T> {

	private ChainArbitrary<T> chainArbitrary;

	public DefaultActionChainArbitrary(
		Supplier<? extends T> initialSupplier,
		List<Tuple2<Integer, ? extends Action<T>>> actionFrequencies
	) {
		List<Tuple2<Integer, TransformerProvider<T>>> providerFrequencies = toProviderFrequencies(actionFrequencies);
		chainArbitrary = new DefaultChainArbitrary<>(initialSupplier, providerFrequencies);
	}

	private List<Tuple2<Integer, TransformerProvider<T>>> toProviderFrequencies(List<Tuple2<Integer, ? extends Action<T>>> actionFrequencies) {
		return actionFrequencies
			.stream()
			.map(frequency -> {
				Action<T> action = frequency.get2();
				// TODO: handle preconditions and Action.provideTransformer and action.toString()
				Transformer<T> transformer = action::run;
				TransformerProvider<T> provider = ignore -> Arbitraries.just(transformer);
				return Tuple.of(frequency.get1(), provider);
			}).collect(Collectors.toList());
	}

	@Override
	public ActionChainArbitrary<T> ofMaxSize(int maxSize) {
		DefaultActionChainArbitrary<T> clone = typedClone();
		clone.chainArbitrary = clone.chainArbitrary.withMaxTransformations(maxSize);
		return clone;
	}

	@Override
	@NotNull
	protected Arbitrary<ActionChain<T>> arbitrary() {
		return chainArbitrary.map(SequentialActionChain::new);
	}
}
