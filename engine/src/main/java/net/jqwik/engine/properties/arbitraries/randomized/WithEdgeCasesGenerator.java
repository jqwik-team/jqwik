package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

import org.jspecify.annotations.*;

class WithEdgeCasesGenerator<T> implements RandomGenerator<T> {

	private final RandomGenerator<T> base;
	private final int baseToEdgeCaseRatio;
	private final RandomGenerator<T> edgeCasesGenerator;

	WithEdgeCasesGenerator(RandomGenerator<T> base, EdgeCases<T> edgeCases, int genSize) {
		this.base = base;
		this.baseToEdgeCaseRatio = calculateBaseToEdgeCaseRatio(genSize, edgeCases.size());
		this.edgeCasesGenerator = chooseEdgeCase(edgeCases);
	}

	@Override
	public Shrinkable<T> next(final Random random) {
		if (random.nextInt(baseToEdgeCaseRatio) == 0) {
			return edgeCasesGenerator.next(random);
		} else {
			return base.next(random);
		}
	}

	private static <T extends @Nullable Object> RandomGenerator<T> chooseEdgeCase(EdgeCases<T> edgeCases) {
		final List<Supplier<Shrinkable<T>>> suppliers = edgeCases.suppliers();
		return random -> RandomGenerators.chooseValue(suppliers, random).get();
	}

	private static int calculateBaseToEdgeCaseRatio(int genSize, int countEdgeCases) {
		return EdgeCasesGenerator.calculateBaseToEdgeCaseRatio(genSize, countEdgeCases);
	}

}
