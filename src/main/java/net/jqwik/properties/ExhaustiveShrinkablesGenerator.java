package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.support.*;

public class ExhaustiveShrinkablesGenerator implements ShrinkablesGenerator {

	public static ExhaustiveShrinkablesGenerator forParameters(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver
	) {
		List<Arbitrary> arbitraries =
			parameters.stream()
					  .map(parameter -> resolveParameter(arbitraryResolver, parameter))
					  .collect(Collectors.toList());

		return new ExhaustiveShrinkablesGenerator(arbitraries);
	}

	private static Arbitrary resolveParameter(ArbitraryResolver arbitraryResolver, MethodParameter parameter) {
		Set<Arbitrary<?>> arbitraries = arbitraryResolver.forParameter(parameter);
		if (arbitraries.isEmpty()) {
			throw new CannotFindArbitraryException(parameter);
		}
		if (arbitraries.size() > 1) {
			String message = String.format("Exhaustive generation requires unambiguous arbitrary for parameter [%s]", parameter);
			throw new JqwikException(message);
		}

		Arbitrary arbitrary = arbitraries.iterator().next();
		if (!arbitrary.exhaustive().isPresent()) {
			String message = String.format("Arbitrary %s does not provide exhaustive generator", arbitrary);
			throw new JqwikException(message);
		}

		return arbitrary;
	}

	private final List<Arbitrary> arbitraries;
	private final List<ExhaustiveGenerator> currentGenerators;

	private ExhaustiveShrinkablesGenerator(List<Arbitrary> arbitraries) {
		this.arbitraries = arbitraries;
		this.currentGenerators = arbitraries.stream().map(arbitrary -> (ExhaustiveGenerator) arbitrary.exhaustive().get()).collect(Collectors.toList());
	}

	@Override
	public boolean hasNext() {
		return currentGenerators.get(0).hasNext();
	}

	@Override
	public List<Shrinkable> next() {
		return Arrays.asList(Shrinkable.unshrinkable(currentGenerators.get(0).next()));
	}

	public long maxCount() {
		return currentGenerators
				   .stream()
				   .mapToLong(ExhaustiveGenerator::maxCount)
				   .reduce((product, count) -> product * count)
				   .getAsLong();
	}

}
