package net.jqwik.properties.arbitraries;

import java.lang.annotation.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.properties.arbitraries.exhaustive.*;
import net.jqwik.properties.arbitraries.randomized.*;

public class OneOfArbitrary<T> implements Arbitrary<T>, SelfConfiguringArbitrary<T> {
	private final List<Arbitrary<T>> all;

	public OneOfArbitrary(List<Arbitrary<T>> all) {this.all = all;}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return RandomGenerators.choose(all).flatMap(Function.identity(), genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive() {
		return ExhaustiveGenerators.choose(all).flatMap(generator -> ExhaustiveGenerators.flatMap(generator, Function.identity()));
	}

	@Override
	public Arbitrary<T> configure(ArbitraryConfigurator configurator, List<Annotation> annotations) {
		for (int i = 0; i < all.size(); i++) {
			Arbitrary<T> arbitrary = all.get(i);
			Arbitrary<T> configuredArbitrary = configurator.configure(arbitrary, annotations);
			all.remove(i);
			all.add(i, configuredArbitrary);
		}
		return this;
	}
}
