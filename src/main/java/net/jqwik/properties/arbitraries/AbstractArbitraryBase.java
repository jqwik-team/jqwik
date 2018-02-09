package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

public abstract class AbstractArbitraryBase<T> implements TargetableArbitrary<T>, Cloneable {

	protected final Class targetClass;
	private double nullProbability = 0.0;

	protected AbstractArbitraryBase(Class<?> targetClass) {
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
	protected <T extends Arbitrary> T typedClone() {
		try {
			return (T) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new JqwikException(e.getMessage());
		}
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
