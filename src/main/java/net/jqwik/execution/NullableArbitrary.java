package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.lang.annotation.*;

public abstract class NullableArbitrary<T> implements Arbitrary<T> {

	private final Class targetClass;
	private double nullProbability = 0.0;

	protected NullableArbitrary(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public RandomGenerator<T> generator(int tries) {
		if (nullProbability > 0.0)
			return baseGenerator(tries).injectNull(nullProbability);
		return baseGenerator(tries);
	}

	protected abstract RandomGenerator<T> baseGenerator(int tries);

	@Override
	public void configure(Annotation configAnnotation) {
		if (configAnnotation instanceof WithNull)
			configure((WithNull) configAnnotation);
	}

	private void configure(WithNull withNull) {
		if (withNull.target().isAssignableFrom(targetClass))
			nullProbability = withNull.value();
	}

}
