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

public class FrequencyOfArbitrary<T> implements Arbitrary<T>, SelfConfiguringArbitrary<T> {

	private final List<Tuple2<Integer, Arbitrary<T>>> frequencies = new ArrayList<>();

	public FrequencyOfArbitrary(List<Tuple2<Integer, Arbitrary<T>>> frequencies) {
		frequencies.stream()
				   .filter(f -> f.get1() > 0)
				   .forEach(this.frequencies::add);
		if (this.frequencies.isEmpty()) {
			throw new JqwikException("At least one frequency must be above 0");
		}
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return RandomGenerators.frequency(frequencies).flatMap(Function.identity(), genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.choose(allArbitraries())
								   .flatMap(generator -> ExhaustiveGenerators.flatMap(generator, Function.identity(), maxNumberOfSamples));
	}

	private List<Arbitrary<T>> allArbitraries() {
		return frequencies.stream().map(Tuple2::get2).collect(Collectors.toList());
	}

	@Override
	public Arbitrary<T> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		frequencies.replaceAll(f -> {
			Arbitrary<T> configuredArbitrary;
			if (f.get2() instanceof SelfConfiguringArbitrary) {
				// TODO: This condition exists 3 times
				//noinspection unchecked
				configuredArbitrary =  ((SelfConfiguringArbitrary) f.get2()).configure(configurator, targetType);
			} else {
				configuredArbitrary = configurator.configure(f.get2(), targetType);
			}
			return Tuple.of(f.get1(), configuredArbitrary);
		});
		return this;
	}
}
