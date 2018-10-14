package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.support.*;

public class ExhaustiveShrinkablesGenerator implements ShrinkablesGenerator {

	public static ExhaustiveShrinkablesGenerator forParameters(List<MethodParameter> parameters, ArbitraryResolver arbitraryResolver) {
		List<ExhaustiveGenerator> exhaustiveGenerators =
			parameters.stream()
					  .map(parameter -> resolveParameter(arbitraryResolver, parameter))
					  .map((Arbitrary arbitrary) -> (ExhaustiveGenerator) arbitrary.exhaustive().get())
					  .collect(Collectors.toList());

		return new ExhaustiveShrinkablesGenerator(exhaustiveGenerators);
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

	private final Iterator<List<Shrinkable>> combinatorialIterator;
	private final long maxCount;


	private ExhaustiveShrinkablesGenerator(List<ExhaustiveGenerator> generators) {
		this.maxCount = generators
			.stream()
			.mapToLong(ExhaustiveGenerator::maxCount)
			.reduce((product, count) -> product * count)
			.getAsLong();

		this.combinatorialIterator = combine(generators);
	}

	private Iterator<List<Shrinkable>> combine(List<ExhaustiveGenerator> generators) {
		List<Iterable> iterables = generators
			.stream()
			.map(g -> (Iterable) g)
			.collect(Collectors.toList());

		return new Iterator<List<Shrinkable>>() {
			Iterator<List> iterator = Combinatorics.combine(iterables);

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
