package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.support.*;

public class ExhaustiveShrinkablesGenerator implements ShrinkablesGenerator {

	public static ExhaustiveShrinkablesGenerator forParameters(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver
	) {
		List<ExhaustiveParameterGenerator> parameterGenerators =
			parameters.stream()
					  .map(parameter -> resolveParameter(arbitraryResolver, parameter))
					  .collect(Collectors.toList());

		return new ExhaustiveShrinkablesGenerator(parameterGenerators);
	}

	private static ExhaustiveParameterGenerator resolveParameter(ArbitraryResolver arbitraryResolver, MethodParameter parameter) {
		Set<ExhaustiveGenerator> generators =
			arbitraryResolver.forParameter(parameter).stream()
							 .map(GenericArbitrary::new)
							 .map(GenericArbitrary::exhaustive)
							 .filter(Optional::isPresent)
							 .map(Optional::get)
							 .collect(Collectors.toSet());

		if (generators.isEmpty()) {
			throw new CannotFindArbitraryException(parameter);
		}
		return new ExhaustiveParameterGenerator(generators);
	}

	private final List<ExhaustiveParameterGenerator> parameterGenerators;

	private ExhaustiveShrinkablesGenerator(List<ExhaustiveParameterGenerator> parameterGenerators) {
		this.parameterGenerators = parameterGenerators;
	}

	@Override
	public boolean hasNext() {
		// Randomized generation should always be able to generate a next set of values
		return true;
	}

	@Override
	public List<Shrinkable> next() {
		return parameterGenerators
				   .stream()
				   .map(generator -> generator.next())
				   .collect(Collectors.toList());
	}

	public long maxCount() {
		return parameterGenerators
				   .stream()
				   .mapToLong(ExhaustiveParameterGenerator::maxCount)
				   .reduce((product, count) -> product * count)
				   .getAsLong();

	}

	private static class ExhaustiveParameterGenerator implements Iterator<Shrinkable> {
		private final List<ExhaustiveGenerator> generators;

		private ExhaustiveParameterGenerator(Set<ExhaustiveGenerator> generators) {
			this.generators = new ArrayList<>(generators);
		}

		@Override
		public boolean hasNext() {
			return generators.get(0).hasNext();
		}

		@Override
		public Shrinkable next() {
			return Shrinkable.unshrinkable(generators.get(0).next());
		}

		private long maxCount() {
			return generators
					   .stream()
					   .mapToLong(ExhaustiveGenerator::maxCount)
					   .reduce((product, count) -> product * count)
					   .getAsLong();
		}

	}
}
