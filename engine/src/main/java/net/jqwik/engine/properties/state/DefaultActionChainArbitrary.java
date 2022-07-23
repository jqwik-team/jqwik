package net.jqwik.engine.properties.state;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.support.*;

public class DefaultActionChainArbitrary<T> extends ArbitraryDecorator<ActionChain<T>> implements ActionChainArbitrary<T> {

	private ChainArbitrary<T> chainArbitrary;

	public DefaultActionChainArbitrary(Supplier<? extends T> initialSupplier) {
		chainArbitrary = new DefaultChainArbitrary<>(initialSupplier);
	}

	private TransformerProvider<T> createProvider(Action<T> action) {
		checkActionIsConsistent(action);
		Optional<Predicate<T>> optionalPrecondition = precondition(action);
		return optionalPrecondition.map(precondition -> TransformerProvider.when(precondition)
																		   .provide(state -> toTransformerArbitrary(action, () -> state)))
								   .orElseGet(() -> supplier -> toTransformerArbitrary(action, supplier));
	}

	@SuppressWarnings("ConstantConditions")
	private Optional<Predicate<T>> precondition(Action<T> action) {
		try {
			Method precondition = preconditionMethod(action.getClass());
			if (!precondition.equals(preconditionMethod(Action.class))) {
				return Optional.of(
					state -> {
						try {
							return (boolean) ReflectionSupport.invokeMethod(precondition, action, state);
						} catch (Exception exception) {
							return JqwikExceptionSupport.throwAsUncheckedException(exception);
						}
					});
			}
		} catch (NoSuchMethodException ignore) {}
		return Optional.empty();
	}

	private void checkActionIsConsistent(Action<T> action) {
		if (!(action instanceof Action.Dependent) && !(action instanceof Action.Independent)) {
			throw new IllegalArgumentException("Action must be of type Action.Dependent or Action.Independent");
		}
	}

	private Arbitrary<Transformer<T>> toTransformerArbitrary(Action<T> action, Supplier<T> stateSupplier) {
		if (action instanceof Action.Independent) {
			Action.Independent<T> independentAction = (Action.Independent<T>) action;
			return independentAction.transformer();
		}
		if (action instanceof Action.Dependent) {
			Action.Dependent<T> dependentAction = (Action.Dependent<T>) action;
			return dependentAction.transformer(stateSupplier.get());
		}
		throw new RuntimeException("Should never get here. Should be caught before by checkActionIsConsistent()");
	}

	private Method preconditionMethod(Class<?> aClass) throws NoSuchMethodException {
		return aClass.getMethod("precondition", Object.class);
	}

	@Override
	public ActionChainArbitrary<T> addAction(int weight, Action<T> action) {
		DefaultActionChainArbitrary<T> clone = typedClone();
		clone.chainArbitrary = clone.chainArbitrary.provideTransformer(weight, createProvider(action));
		return clone;
	}

	@Override
	public ActionChainArbitrary<T> withMaxTransformations(int maxSize) {
		DefaultActionChainArbitrary<T> clone = typedClone();
		clone.chainArbitrary = clone.chainArbitrary.withMaxTransformations(maxSize);
		return clone;
	}

	@Override
	public ActionChainArbitrary<T> improveShrinkingWith(Supplier<ChangeDetector<T>> changeDetectorSupplier) {
		DefaultActionChainArbitrary<T> clone = typedClone();
		clone.chainArbitrary = clone.chainArbitrary.improveShrinkingWith(changeDetectorSupplier);
		return clone;
	}

	@Override
	protected Arbitrary<ActionChain<T>> arbitrary() {
		return chainArbitrary.map(SequentialActionChain::new);
	}

	@Override
	public boolean isGeneratorMemoizable() {
		// Not memoizable, because any non-memoizable arbitrary could be used in actions
		return false;
	}
}
