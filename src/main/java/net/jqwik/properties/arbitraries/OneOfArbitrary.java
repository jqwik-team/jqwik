package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;

import java.lang.annotation.*;
import java.util.*;
import java.util.function.*;

public class OneOfArbitrary<T> implements Arbitrary<T>, SelfConfiguringArbitrary<T> {
	private final List<Arbitrary<T>> all;

	public OneOfArbitrary(List<Arbitrary<T>> all) {this.all = all;}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return RandomGenerators.choose(all).flatMap(Function.identity(), genSize);
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
