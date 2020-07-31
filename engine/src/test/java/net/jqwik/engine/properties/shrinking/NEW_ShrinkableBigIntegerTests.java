package net.jqwik.engine.properties.shrinking;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.NEW_ShrinkingTestHelper.*;

@Group
@Label("ShrinkableBigInteger")
class NEW_ShrinkableBigIntegerTests {

	@Example
	void creation() {
		Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(25, Range.of(-100L, 100L));
		assertThat(shrinkable.value()).isEqualTo(BigInteger.valueOf(25));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(25));
	}

	@Example
	void cannotCreateValueOutsideRange() {
		assertThatThrownBy(
			() -> createShrinkableBigInteger(25, Range.of(50L, 100L)))
			.isInstanceOf(JqwikException.class);
	}

	@Example
	void shrinkingDistanceIsDistanceToShrinkingTarget() {
		assertThat(
			createShrinkableBigInteger(25, Range.of(-100L, 100L)).distance())
			.isEqualTo(ShrinkingDistance.of(25));
		assertThat(
			createShrinkableBigInteger(-25, Range.of(-100L, 100L)).distance())
			.isEqualTo(ShrinkingDistance.of(25));
		assertThat(
			createShrinkableBigInteger(25, Range.of(5L, 100L)).distance())
			.isEqualTo(ShrinkingDistance.of(20));
		assertThat(
			createShrinkableBigInteger(-25, Range.of(-100L, -5L)).distance())
			.isEqualTo(ShrinkingDistance.of(20));
	}

	@Example
	void shrinkingDistanceWithExplicitShrinkingTarget() {
		assertThat(
			createShrinkableBigInteger(25, Range.of(-100L, 100L), 100L).distance())
			.isEqualTo(ShrinkingDistance.of(75));
		assertThat(
			createShrinkableBigInteger(-25, Range.of(-100L, 100L), -100L).distance())
			.isEqualTo(ShrinkingDistance.of(75));
		assertThat(
			createShrinkableBigInteger(-25, Range.of(-100L, 100L), 100L).distance())
			.isEqualTo(ShrinkingDistance.of(125));
	}

	@Example
	void shrinkingDistanceOutsideLongRange() {
		Range<BigInteger> bigIntegerRange = Range.of(
			new BigInteger("-1000000000000000000000"),
			new BigInteger("1000000000000000000000")
		);

		assertThat(
			new ShrinkableBigInteger(new BigInteger("99999999999999999999"), bigIntegerRange, BigInteger.ZERO).distance())
			.isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE));

		assertThat(
			new ShrinkableBigInteger(new BigInteger("-99999999999999999999"), bigIntegerRange, BigInteger.ZERO).distance())
			.isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE));
	}

	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(100000, Range.of(5L, 500000L));

			TestingFalsifier<BigInteger> falsifier = aBigInteger -> aBigInteger.compareTo(BigInteger.valueOf(1000)) <= 0;
			BigInteger shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(BigInteger.valueOf(1001));
		}

		@Example
		void withFilter() {
			Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(100000, Range.of(0L, 1000000L));

			TestingFalsifier<BigInteger> falsifier = aBigInteger -> aBigInteger.intValueExact() < 99;
			Falsifier<BigInteger> filteredFalsifier = falsifier.withFilter(aBigInteger -> aBigInteger.intValueExact() % 2 == 0);

			BigInteger shrunkValue = shrinkToMinimal(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo(BigInteger.valueOf(100));
		}

		@Example
		void upToExplicitShrinkingTarget() {
			Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(1000, Range.of(5L, 500000L), 5000L);

			TestingFalsifier<BigInteger> falsifier = aBigInteger -> aBigInteger.compareTo(BigInteger.valueOf(5000)) >= 0;
			BigInteger shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(BigInteger.valueOf(4999));
		}

	}

	private Shrinkable<BigInteger> createShrinkableBigInteger(long number, Range<Long> longRange) {
		Range<BigInteger> bigIntegerRange = longRange.map(BigInteger::valueOf);
		return new ShrinkableBigInteger(
			BigInteger.valueOf(number),
			bigIntegerRange,
			RandomIntegralGenerators.defaultShrinkingTarget(bigIntegerRange)
		);
	}

	private Shrinkable<BigInteger> createShrinkableBigInteger(long number, Range<Long> longRange, long shrinkingTarget) {
		Range<BigInteger> bigIntegerRange = longRange.map(BigInteger::valueOf);
		return new ShrinkableBigInteger(BigInteger.valueOf(number), bigIntegerRange, BigInteger.valueOf(shrinkingTarget));
	}

}
