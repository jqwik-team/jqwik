package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.support.*;

public class ExhaustiveShrinkablesGenerator implements ShrinkablesGenerator {

	public static ExhaustiveShrinkablesGenerator forParameters(List<MethodParameter> parameters, ArbitraryResolver arbitraryResolver) {
		List<List<ExhaustiveGenerator>> exhaustiveGenerators =
			parameters.stream()
					  .map(parameter -> resolveParameter(arbitraryResolver, parameter))
					  .collect(Collectors.toList());

		return new ExhaustiveShrinkablesGenerator(exhaustiveGenerators);
	}

	private static List<ExhaustiveGenerator> resolveParameter(ArbitraryResolver arbitraryResolver, MethodParameter parameter) {
		Set<Arbitrary<?>> arbitraries = arbitraryResolver.forParameter(parameter);
		if (arbitraries.isEmpty()) {
			throw new CannotFindArbitraryException(parameter);
		}

		List<ExhaustiveGenerator> exhaustiveGenerators = new ArrayList<>();
		for (Arbitrary arbitrary : arbitraries) {
			@SuppressWarnings("unchecked")
			Optional<ExhaustiveGenerator> optionalGenerator = arbitrary.exhaustive();
			if (!optionalGenerator.isPresent()) {
				String message = String.format("Arbitrary %s does not provide exhaustive generator", arbitrary);
				throw new JqwikException(message);
			}
			exhaustiveGenerators.add(optionalGenerator.get());
		}
		return exhaustiveGenerators;

	}

	private final Iterator<List<Shrinkable>> combinatorialIterator;
	private final long maxCount;

	private ExhaustiveShrinkablesGenerator(List<List<ExhaustiveGenerator>> generators) {
		this.maxCount = generators
							.stream()
							.mapToLong(set -> set.stream().mapToLong(ExhaustiveGenerator::maxCount).sum())
							.reduce((product, count) -> product * count)
							.orElse(1L);

		this.combinatorialIterator = combine(generators);
	}

	private Iterator<List<Shrinkable>> combine(List<List<ExhaustiveGenerator>> generators) {
		List<Iterable<Object>> iterables = generators
											   .stream()
											   .map(this::concat)
											   .collect(Collectors.toList());

		return new Iterator<List<Shrinkable>>() {
			Iterator<List<Object>> iterator = Combinatorics.combine(iterables);

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public List<Shrinkable> next() {
				List<Shrinkable> values = new ArrayList<>();
				for (Object o : iterator.next()) {
					values.add(Shrinkable.unshrinkable(o));
				}
				return values;
			}
		};
	}

	@SuppressWarnings("unchecked")
	private Iterable<Object> concat(List<ExhaustiveGenerator> generatorList) {
		List<Iterable<Object>> iterables = generatorList
											   .stream()
											   .map(g -> (Iterable<Object>) g)
											   .collect(Collectors.toList());
		return () -> Combinatorics.concat(iterables);
	}

	@Override
	public boolean hasNext() {
		return combinatorialIterator.hasNext();
	}

	@Override
	public List<Shrinkable> next() {
		return combinatorialIterator.next();
	}

	public long maxCount() {
		return maxCount;
	}

}
