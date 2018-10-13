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
							 .map(genericArbitrary -> {
								 Optional<ExhaustiveGenerator<Object>> exhaustive = genericArbitrary.exhaustive();
								 if (exhaustive.isPresent()) {
									 return exhaustive.get();
								 } else {
									 String message = String.format("Arbitrary %s does not provide exhaustive generator", genericArbitrary);
									 throw new JqwikException(message);
								 }
							 })
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
		return parameterGenerators.stream().allMatch(ExhaustiveParameterGenerator::hasNext);
	}

	@Override
	public List<Shrinkable> next() {
		return parameterGenerators
				   .stream()
				   .map(ExhaustiveParameterGenerator::next)
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
