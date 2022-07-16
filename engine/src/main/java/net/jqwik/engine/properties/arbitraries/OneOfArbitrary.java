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
	private final boolean isGeneratorMemoizable;

	@SuppressWarnings("unchecked")
	public OneOfArbitrary(Collection<Arbitrary<? extends T>> choices) {
		for (Arbitrary<? extends T> choice : choices) {
			all.add((Arbitrary<T>) choice);
		}
		isGeneratorMemoizable = all.stream().allMatch(Arbitrary::isGeneratorMemoizable);
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return rawGeneration(genSize, false);
	}

	@Override
	public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
		return rawGeneration(genSize, true);
	}

	@Override
	public boolean isGeneratorMemoizable() {
		return isGeneratorMemoizable;
	}

	private RandomGenerator<T> rawGeneration(int genSize, boolean withEmbeddedEdgeCases) {
		List<Tuple2<Integer, Arbitrary<T>>> frequencies =
			all.stream()
			   .map(a -> Tuple.of(1, a))
			   .collect(Collectors.toList());
		return RandomGenerators.frequencyOf(frequencies, genSize, withEmbeddedEdgeCases);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.choose(all, maxNumberOfSamples)
								   .flatMap(generator -> ExhaustiveGenerators
									   .flatMap(generator, Function.identity(), maxNumberOfSamples));
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.concatFrom(all, maxEdgeCases);
	}

	@Override
	public Arbitrary<T> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		all.replaceAll(arbitrary -> SelfConfiguringArbitrary.configure(arbitrary, configurator, targetType));
		return configurator.configure(this, targetType);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OneOfArbitrary<?> that = (OneOfArbitrary<?>) o;
		return all.equals(that.all);
	}

	@Override
	public int hashCode() {
		return all.hashCode();
	}
}
