package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class OneOfArbitrary<T> implements Arbitrary<T>, SelfConfiguringArbitrary<T> {
	private final List<Arbitrary<T>> all = new ArrayList<>();

	public OneOfArbitrary(List<Arbitrary<T>> all) {this.all.addAll(all);}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		List<Tuple2<Integer, Arbitrary<T>>> frequencies =
			all.stream()
			   .map(a -> Tuple.of(1, a))
			   .collect(Collectors.toList());
		return RandomGenerators.frequencyOf(frequencies, genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.choose(all, maxNumberOfSamples)
								   .flatMap(generator -> ExhaustiveGenerators
									   .flatMap(generator, Function.identity(), maxNumberOfSamples));
	}

	@Override
	public EdgeCases<T> edgeCases() {
		List<EdgeCases<T>> allEdgeCases = all.stream().map(Arbitrary::edgeCases).collect(Collectors.toList());
		return EdgeCases.concat(allEdgeCases);
	}

	@Override
	public Arbitrary<T> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		all.replaceAll(arbitrary -> SelfConfiguringArbitrary.configure(arbitrary, configurator, targetType));
		return this;
	}

	protected List<Arbitrary<T>> arbitraries() {
		return all;
	}

	protected void addArbitrary(Arbitrary<T> arbitrary) {
		all.add(arbitrary);
	}
}
