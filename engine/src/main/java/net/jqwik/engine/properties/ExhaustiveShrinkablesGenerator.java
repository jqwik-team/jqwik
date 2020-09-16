package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

public class ExhaustiveShrinkablesGenerator implements ForAllParametersGenerator {

	public static ExhaustiveShrinkablesGenerator forParameters(
		List<MethodParameter> parameters,
		ArbitraryResolver arbitraryResolver,
		long maxNumberOfSamples
	) {
		List<List<ExhaustiveGenerator<Object>>> exhaustiveGenerators =
			parameters.stream()
					  .map(parameter -> resolveParameter(arbitraryResolver, parameter, maxNumberOfSamples))
					  .collect(Collectors.toList());

		return new ExhaustiveShrinkablesGenerator(exhaustiveGenerators);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static List<ExhaustiveGenerator<Object>> resolveParameter(
		ArbitraryResolver arbitraryResolver,
		MethodParameter parameter,
		long maxNumberOfSamples
	) {
		Set<Arbitrary<?>> arbitraries = arbitraryResolver.forParameter(parameter);
		if (arbitraries.isEmpty()) {
			throw new CannotFindArbitraryException(TypeUsageImpl.forParameter(parameter), parameter.getAnnotation(ForAll.class));
		}

		List<ExhaustiveGenerator<Object>> exhaustiveGenerators = new ArrayList<>();
		for (Arbitrary arbitrary : arbitraries) {
			Optional<ExhaustiveGenerator<Object>> optionalGenerator = arbitrary.exhaustive(maxNumberOfSamples);
			if (!optionalGenerator.isPresent()) {
				String message = String.format("Arbitrary %s does not provide exhaustive generator", arbitrary);
				throw new JqwikException(message);
			}
			exhaustiveGenerators.add(optionalGenerator.get());
		}
		return exhaustiveGenerators;

	}

	private final Iterator<List<Shrinkable<Object>>> combinatorialIterator;
	private final long maxCount;

	private ExhaustiveShrinkablesGenerator(List<List<ExhaustiveGenerator<Object>>> generators) {
		this.maxCount = generators
							.stream()
							.mapToLong(set -> set.stream().mapToLong(ExhaustiveGenerator::maxCount).sum())
							.reduce((product, count) -> product * count)
							.orElse(1L);

		this.combinatorialIterator = combine(generators);
	}

	private Iterator<List<Shrinkable<Object>>> combine(List<List<ExhaustiveGenerator<Object>>> generators) {
		List<Iterable<Object>> iterables = generators
											   .stream()
											   .map(this::concat)
											   .collect(Collectors.toList());

		return new Iterator<List<Shrinkable<Object>>>() {
			final Iterator<List<Object>> iterator = Combinatorics.combine(iterables);

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public List<Shrinkable<Object>> next() {
				List<Shrinkable<Object>> values = new ArrayList<>();
				for (Object o : iterator.next()) {
					values.add(Shrinkable.unshrinkable(o));
				}
				return values;
			}
		};
	}

	private Iterable<Object> concat(List<ExhaustiveGenerator<Object>> generatorList) {
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
	public List<Shrinkable<Object>> next() {
		return combinatorialIterator.next();
	}

	public long maxCount() {
		return maxCount;
	}

}
