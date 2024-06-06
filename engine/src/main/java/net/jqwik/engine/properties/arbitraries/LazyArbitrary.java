package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

public class LazyArbitrary<T> implements Arbitrary<T>, SelfConfiguringArbitrary<T> {
	private final Supplier<? extends Arbitrary<T>> arbitrarySupplier;
	private final List<Tuple.Tuple2<ArbitraryConfigurator, TypeUsage>> configurations = new ArrayList<>();
	private Arbitrary<T> arbitrary;

	public LazyArbitrary(Supplier<? extends Arbitrary<T>> arbitrarySupplier) {
		this.arbitrarySupplier = arbitrarySupplier;
	}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return getArbitrary().generator(genSize);
	}

	@Override
	public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
		// This is actually right. Don't use getArbitrary().generatorWithEmbeddedEdgeCases() directly
		return getArbitrary().generator(genSize, true);
	}

	private Arbitrary<T> getArbitrary() {
		if (this.arbitrary == null) {
			Arbitrary<T> rawArbitrary = arbitrarySupplier.get();
			for (Tuple.Tuple2<ArbitraryConfigurator, TypeUsage> configuration : configurations) {
				ArbitraryConfigurator configurator = configuration.get1();
				TypeUsage targetType = configuration.get2();
				rawArbitrary = SelfConfiguringArbitrary.configure(rawArbitrary, configurator, targetType);
			}
			this.arbitrary = rawArbitrary;
		}
		return this.arbitrary;
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return getArbitrary().exhaustive(maxNumberOfSamples);
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		// Cannot be delegated to getArbitrary() due to possible recursion
		return EdgeCases.none();
	}

	@Override
	public Arbitrary<T> configure(ArbitraryConfigurator configurator, TypeUsage targetType) {
		configurations.add(Tuple.of(configurator, targetType));
		return this;
	}
}
