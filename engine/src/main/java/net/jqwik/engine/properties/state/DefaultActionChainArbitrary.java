package net.jqwik.engine.properties.state;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.support.*;

public class DefaultActionChainArbitrary<T> extends ArbitraryDecorator<ActionChain<T>> implements ActionChainArbitrary<T> {

	private ChainArbitrary<T> chainArbitrary;

	public DefaultActionChainArbitrary(
		Supplier<? extends T> initialSupplier,
		List<Tuple2<Integer, Action<T>>> actionFrequencies
	) {
		List<Tuple2<Integer, TransformerProvider<T>>> providerFrequencies = toProviderFrequencies(actionFrequencies);
		chainArbitrary = new DefaultChainArbitrary<>(initialSupplier, providerFrequencies);
	}

	private List<Tuple2<Integer, TransformerProvider<T>>> toProviderFrequencies(List<Tuple2<Integer, Action<T>>> actionFrequencies) {
		return actionFrequencies
			.stream()
			.map(frequency -> {
				TransformerProvider<T> provider = createProvider(frequency.get2());
				return Tuple.of(frequency.get1(), provider);
			}).collect(Collectors.toList());
	}

	private TransformerProvider<T> createProvider(Action<T> action) {
		return stateSupplier -> {
			if (!checkPrecondition(stateSupplier, action)) {
				return null;
			}
			return toTransformerArbitrary(action, stateSupplier);
		};
	}

	private boolean checkPrecondition(Supplier<T> stateSupplier, Action<T> action) {
		try {
			Method precondition = precondition(action.getClass());
			if (!precondition.equals(precondition(Action.class))) {
				try {
					boolean isApplicable = (boolean) ReflectionSupport.invokeMethod(precondition, action, stateSupplier.get());
					if (!isApplicable) {
						return false;
					}
				} catch (Exception exception) {
					JqwikExceptionSupport.throwAsUncheckedException(exception);
				}
			}
		} catch (NoSuchMethodException ignore) {}
		return true;
	}

	private Arbitrary<Transformer<T>> toTransformerArbitrary(Action<T> action, Supplier<T> stateSupplier) {
		// TODO: handle Action.transformer(state) and action.toString()
		return action.transformer();
	}

	private Method precondition(Class<?> aClass) throws NoSuchMethodException {
		return aClass.getMethod("precondition", Object.class);
	}

	@Override
	public ActionChainArbitrary<T> withMaxActions(int maxSize) {
		DefaultActionChainArbitrary<T> clone = typedClone();
		clone.chainArbitrary = clone.chainArbitrary.withMaxTransformations(maxSize);
		return clone;
	}

	@Override
	protected Arbitrary<ActionChain<T>> arbitrary() {
		return chainArbitrary.map(SequentialActionChain::new);
	}
}
