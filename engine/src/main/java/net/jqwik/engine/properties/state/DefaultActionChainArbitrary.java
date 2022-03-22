package net.jqwik.engine.properties.state;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.arbitraries.ArbitraryDecorator;
import net.jqwik.api.state.Action;
import net.jqwik.api.state.ActionChain;
import net.jqwik.api.state.ActionChainArbitrary;
import net.jqwik.api.state.ChainArbitrary;
import net.jqwik.api.state.Transformer;
import net.jqwik.api.state.TransformerProvider;
import net.jqwik.engine.support.JqwikExceptionSupport;

public class DefaultActionChainArbitrary<T> extends ArbitraryDecorator<ActionChain<T>> implements ActionChainArbitrary<T> {

	private ChainArbitrary<T> chainArbitrary;

	public DefaultActionChainArbitrary(
		Supplier<? extends T> initialSupplier,
		List<Tuple2<Integer, Arbitrary<Action<T>>>> actionArbitraryFrequencies
	) {
		List<Tuple2<Integer, TransformerProvider<T>>> providerFrequencies = toProviderFrequencies(actionArbitraryFrequencies);
		chainArbitrary = new DefaultChainArbitrary<>(initialSupplier, providerFrequencies);
	}

	private List<Tuple2<Integer, TransformerProvider<T>>> toProviderFrequencies(List<Tuple2<Integer, Arbitrary<Action<T>>>> actionFrequencies) {
		return actionFrequencies
			.stream()
			.map(frequency -> {
				TransformerProvider<T> provider = createProvider(frequency.get2());
				return Tuple.of(frequency.get1(), provider);
			}).collect(Collectors.toList());
	}

	@NotNull
	private TransformerProvider<T> createProvider(Arbitrary<? extends Action<T>> actionArbitrary) {
		return stateSupplier -> actionArbitrary.flatMap(action -> {
			// TODO: handle preconditions and Action.provideTransformer and action.toString()
			if (!checkPrecondition(stateSupplier, action)) {
				return null;
			}
			return Arbitraries.just(toTransformer(action));
		});
	}

	private boolean checkPrecondition(Supplier<T> stateSupplier, Action<T> action) {
		try {
			Method precondition = precondition(action.getClass());
			if (!precondition.equals(precondition(Action.class))) {
				try {
					boolean isApplicable = (boolean) precondition.invoke(action, stateSupplier.get());
					if (!isApplicable) {
						return false;
					}
				} catch (InvocationTargetException invocationTargetException) {
					JqwikExceptionSupport.throwAsUncheckedException(invocationTargetException.getTargetException());
				} catch (Exception exception) {
					JqwikExceptionSupport.throwAsUncheckedException(exception);
				}
			}
		} catch (NoSuchMethodException ignore) {}
		return true;
	}

	@NotNull
	private Transformer<T> toTransformer(Action<T> action) {
		return new Transformer<T>() {
			@Override
			public @NotNull T apply(@NotNull T state) {
				return action.run(state);
			}

			@Override
			public String toString() {
				return action.toString();
			}
		};
	}

	@NotNull
	private Method precondition(Class<?> aClass) throws NoSuchMethodException {
		return aClass.getMethod("precondition", Object.class);
	}

	@Override
	@NotNull
	public ActionChainArbitrary<T> withMaxActions(int maxSize) {
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
