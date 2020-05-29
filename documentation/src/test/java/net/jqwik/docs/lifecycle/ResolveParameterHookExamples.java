package net.jqwik.docs.lifecycle;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

@AddLifecycleHook(value = CalculatorResolver.class, propagateTo = PropagationMode.ALL_DESCENDANTS)
class ResolveParameterHookExamples {

	@Property
	void addingANumberTwice(@ForAll int aNumber, Calculator calculator) {
		calculator.plus(aNumber);
		calculator.plus(aNumber);
		Assertions.assertThat(calculator.result()).isEqualTo(aNumber * 2);
	}
}

class CalculatorResolver implements ResolveParameterHook {
	@Override
	public Optional<ParameterSupplier> resolve(
		final ParameterResolutionContext parameterContext,
		final LifecycleContext lifecycleContext
	) {
		return Optional.of(optionalTry -> new Calculator());
	}

	@Override
	public PropagationMode propagateTo() {
		// Allow annotation on container level
		return PropagationMode.ALL_DESCENDANTS;
	}
}

class Calculator {
	private int result = 0;

	public int result() {
		return result;
	}

	public void plus(int addend) {
		result += addend;
	}
}
