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
		checkActionIsConsistent(action);
		return stateSupplier -> {
			if (!checkPrecondition(stateSupplier, action)) {
				return null;
			}
			return toTransformerArbitrary(action, stateSupplier);
		};
	}

	private boolean checkPrecondition(Supplier<T> stateSupplier, Action<T> action) {
		try {
			Method precondition = preconditionMethod(action.getClass());
			if (!precondition.equals(preconditionMethod(Action.class))) {
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

	private void checkActionIsConsistent(Action<T> action) {
		Optional<Method> transformer = transformerMethod(action.getClass());
		Optional<Method> transformerWithStateAccess = transformerStateMethod(action.getClass());

		boolean noTransformerMethodImplemented = !transformer.isPresent() && !transformerWithStateAccess.isPresent();
		boolean bothTransformerMethodImplemented = transformer.isPresent() && transformerWithStateAccess.isPresent();
		if (noTransformerMethodImplemented || bothTransformerMethodImplemented) {
			String message = String.format("Action %s must implement exactly one of transformer() or transformer(state).", action);
			throw new JqwikException(message);
		}
	}

	@SuppressWarnings("unchecked")
	private Arbitrary<Transformer<T>> toTransformerArbitrary(Action<T> action, Supplier<T> stateSupplier) {
		Optional<Method> transformer = transformerMethod(action.getClass());
		Optional<Method> transformerWithStateAccess = transformerStateMethod(action.getClass());

		if (transformer.isPresent()) {
			if (!transformerWithStateAccess.isPresent()) {
				return (Arbitrary<Transformer<T>>) ReflectionSupport.invokeMethod(transformer.get(), action);
			}
		} else {
			if (transformerWithStateAccess.isPresent()) {
				return (Arbitrary<Transformer<T>>) ReflectionSupport.invokeMethod(
					transformerWithStateAccess.get(), action,
					stateSupplier.get()
				);
			}
		}

		throw new RuntimeException("Should never get here. Should be caught before by checkActionIsConsistent()");
	}

	private JqwikException inconsistentActionError(Action<T> action) {
		String message = String.format("Action %s must implement exactly one of transformer() or transformer(state).", action);
		return new JqwikException(message);
	}

	private Optional<Method> transformerMethod(Class<?> aClass) {
		try {
			Method method = aClass.getMethod("transformer");
			if (!method.equals(Action.class.getMethod("transformer"))) {
				return Optional.of(method);
			}
		} catch (NoSuchMethodException ignore) {}
		return Optional.empty();
	}

	private Optional<Method> transformerStateMethod(Class<?> aClass) {
		try {
			Method method = aClass.getMethod("transformer", Object.class);
			if (!method.equals(Action.class.getMethod("transformer", Object.class))) {
				return Optional.of(method);
			}
		} catch (NoSuchMethodException ignore) {}
		return Optional.empty();
	}

	private Method preconditionMethod(Class<?> aClass) throws NoSuchMethodException {
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
