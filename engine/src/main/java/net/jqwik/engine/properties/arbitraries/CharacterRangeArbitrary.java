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
		return RandomGenerators.chars(min, max).withEdgeCases(genSize, edgeCases());
	}

	private List<Shrinkable<Character>> listOfEdgeCases() {
		return Stream.of(min, max)
					 .map(aCharacter -> new ShrinkableBigInteger(
							 BigInteger.valueOf((int) aCharacter),
							 Range.of(BigInteger.valueOf(min), BigInteger.valueOf(max)),
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
