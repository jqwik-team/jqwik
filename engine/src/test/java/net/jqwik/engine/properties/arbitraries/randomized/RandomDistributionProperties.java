package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.RandomDistribution.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.constraints.*;

public class RandomDistributionProperties {

	@Property(edgeCases = EdgeCasesMode.MIXIN)
	void onlyGenerateValuesWithinRange(
		@ForAll("distributions") RandomDistribution distribution,
		@ForAll @IntRange(min = 1, max = 10000) int genSize,
		@ForAll("distributionConfigValues") Tuple3<BigInteger, BigInteger, BigInteger> minMaxCenter,
		@ForAll JqwikRandom random
	) {
		BigInteger min = minMaxCenter.get1();
		BigInteger max = minMaxCenter.get2();
		BigInteger center = minMaxCenter.get3();

		RandomNumericGenerator generator = distribution.createGenerator(genSize, min, max, center);

		for (int i = 0; i < 50; i++) {
			BigInteger value = generator.next(random);
			Assertions.assertThat(value).isBetween(min, max);
		}
	}

	@Property(tries = 100, edgeCases = EdgeCasesMode.NONE)
	void generateSameValueForSameRandomSeed(
		@ForAll("distributions") RandomDistribution distribution,
		@ForAll @IntRange(min = 1, max = 10000) int genSize,
		@ForAll("distributionConfigValues") Tuple3<BigInteger, BigInteger, BigInteger> minMaxCenter,
		@ForAll long randomSeed
	) {
		Assume.that(randomSeed != 0L);

		BigInteger min = minMaxCenter.get1();
		BigInteger max = minMaxCenter.get2();
		BigInteger center = minMaxCenter.get3();

		RandomNumericGenerator generator = distribution.createGenerator(genSize, min, max, center);

		// BigInteger value1 = generator.next(new Random(randomSeed));
		// BigInteger value2 = generator.next(new Random(randomSeed));
		// Assertions.assertThat(value1).isEqualTo(value2);
	}

	@Provide
	Arbitrary<RandomDistribution> distributions() {
		return Arbitraries.oneOf(
				Arbitraries.just(RandomDistribution.uniform()),
				Arbitraries.just(RandomDistribution.biased()),
			Arbitraries.doubles().between(0.1, 5.0).ofScale(1).map(RandomDistribution::gaussian)
		);
	}

	@Provide
	Arbitrary<Tuple3<BigInteger, BigInteger, BigInteger>> distributionConfigValues() {
		Arbitrary<BigInteger> mins = Arbitraries
										 .bigIntegers()
										 .between(BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE / 2));
		return mins.flatMap(min -> {
			Arbitrary<BigInteger> maxs = Arbitraries
											 .bigIntegers()
											 .between(min, BigInteger.valueOf(Long.MAX_VALUE));
			return maxs.flatMap(max -> {
				Arbitrary<BigInteger> centers = Arbitraries.bigIntegers().between(min, max);
				return centers.map(center -> Tuple.of(min, max, center));
			});
		});
	}
}
