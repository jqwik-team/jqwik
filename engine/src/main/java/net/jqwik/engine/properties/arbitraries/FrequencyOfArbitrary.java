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

import org.jspecify.annotations.*;

public class FrequencyOfArbitrary<T extends @Nullable Object> implements Arbitrary<T>, SelfConfiguringArbitrary<T> {

	private final List<Tuple2<Integer, ? extends Arbitrary<T>>> frequencies;
	private final boolean isGeneratorMemoizable;

	public FrequencyOfArbitrary(List<Tuple2<Integer, ? extends Arbitrary<T>>> frequencies) {
		this.frequencies = frequencies;
		this.isGeneratorMemoizable = frequencies.stream().allMatch(t -> t.get2().isGeneratorMemoizable());
		if (this.frequencies.isEmpty()) {
			throw new JqwikException("At least one frequency must be above 0");
		}
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return RandomGenerators.frequencyOf(frequencies, genSize, false);
	}

	@Override
	public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
		return RandomGenerators.frequencyOf(frequencies, genSize, true);
	}

	@Override
	public boolean isGeneratorMemoizable() {
		return isGeneratorMemoizable;
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators
				   .choose(allArbitraries(), maxNumberOfSamples)
				   .flatMap(generator -> ExhaustiveGenerators
											 .flatMap(generator, Function.identity(), maxNumberOfSamples));
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.concatFrom(allArbitraries(), maxEdgeCases);
	}

	private List<Arbitrary<T>> allArbitraries() {
		return frequencies.stream().map(Tuple2::get2).collect(Collectors.toList());
	}

	@Override
	public Arbitrary<T> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		Arbitrary<T> configuredArbitrary = configurator.configure(this, targetType);
		if (this == configuredArbitrary) {
			// Only hand configuration down to arbitraries if configurator did not apply to frequencyOf arbitrary itself
			frequencies.replaceAll(f -> {
				Arbitrary<T> configuredComponentArbitrary = SelfConfiguringArbitrary.configure(f.get2(), configurator, targetType);
				return Tuple.of(f.get1(), configuredComponentArbitrary);
			});
		}
		return configuredArbitrary;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FrequencyOfArbitrary<?> that = (FrequencyOfArbitrary<?>) o;
		return frequencies.equals(that.frequencies);
	}

	@Override
	public int hashCode() {
		return frequencies.hashCode();
	}
}
