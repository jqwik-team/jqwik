package net.jqwik.properties;

public abstract class ArbitraryWrapper<T> implements Arbitrary<T> {

	protected final Arbitrary<T> wrapped;

	public ArbitraryWrapper(Arbitrary<T> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public RandomGenerator<T> generator(int tries) {
		return wrapped.generator(tries);
	}

}
