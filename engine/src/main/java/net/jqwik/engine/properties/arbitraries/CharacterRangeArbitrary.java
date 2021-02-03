package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

public class CharacterRangeArbitrary implements Arbitrary<Character> {
	private final char min;
	private final char max;

	public CharacterRangeArbitrary(char min, char max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public RandomGenerator<Character> generator(int genSize) {
		return RandomGenerators.chars(min, max);
	}

	private List<Shrinkable<Character>> listOfEdgeCases() {
		Stream<Character> edgeCases = Stream.of(min, max, ' ').filter(c -> c >= min && c <= max);
		return edgeCases
					   .map(aCharacter -> new ShrinkableBigInteger(
									BigInteger.valueOf((int) aCharacter),
									Range.of(BigInteger.valueOf(this.min), BigInteger.valueOf(max)),
									BigInteger.valueOf(min)
							)
					   )
					   .map(shrinkableBigInteger -> shrinkableBigInteger.map(BigInteger::intValueExact))
					   .map(shrinkableInteger -> shrinkableInteger.map(anInt -> ((char) (int) anInt)))
					   .collect(Collectors.toList());
	}

	@Override
	public Optional<ExhaustiveGenerator<Character>> exhaustive(long maxNumberOfSamples) {
		long maxCount = max + 1 - min;
		return ExhaustiveGenerators
					   .fromIterable(() -> IntStream.range(min, max + 1).iterator(), maxCount, maxNumberOfSamples)
					   .map(optionalGenerator -> optionalGenerator.map(anInt -> (char) (int) anInt));
	}

	@Override
	public EdgeCases<Character> edgeCases() {
		return EdgeCasesSupport.fromShrinkables(listOfEdgeCases());
	}
}
