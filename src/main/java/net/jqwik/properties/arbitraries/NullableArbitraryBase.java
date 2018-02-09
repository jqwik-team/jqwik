package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

public abstract class NullableArbitraryBase<T> implements NullableArbitrary<T>, Cloneable {

	protected final Class targetClass;
	private double nullProbability = 0.0;

	protected NullableArbitraryBase(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public RandomGenerator<T> generator(int tries) {
		if (nullProbability > 0.0)
			return baseGenerator(tries).injectNull(nullProbability);
		return baseGenerator(tries);
	}

	@Override
	public Class getTargetClass() {
		return targetClass;
	}

	protected abstract RandomGenerator<T> baseGenerator(int tries);

	@SuppressWarnings("unchecked")
	protected <A extends Arbitrary> A typedClone() {
		try {
			return (A) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new JqwikException(e.getMessage());
		}
	}

	public NullableArbitraryBase<T> withNull(double nullProbability) {
		NullableArbitraryBase<T> clone = this.typedClone();
		clone.nullProbability = nullProbability;
		return clone;
	}

	// TODO: Remove if all arbitrary providers extend from NullableArbitraryProvider
	public void configure(WithNull withNull) {
		if (withNull.target().isAssignableFrom(targetClass))
			nullProbability = withNull.value();
	}

	// TODO: Remove if all arbitrary providers extend from NullableArbitraryProvider
	public double getNullProbability() {
		return nullProbability;
	}
}
