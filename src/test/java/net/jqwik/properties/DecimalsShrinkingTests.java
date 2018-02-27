package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.math.*;
import java.util.*;

import org.assertj.core.data.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.properties.arbitraries.*;

@Group
class DecimalsShrinkingTests {

	@Example
	void valueOutsideRangeDoesNotShrink() {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-10.0), new BigDecimal(10.0), 2);
		assertThat(shrinker.nextCandidates(new BigDecimal(20.0))).isEmpty();
	}

	@Example
	void shrinkFrom0DoesNotShrink() {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-10.0), new BigDecimal(10.0), 2);
		assertThat(shrinker.nextCandidates(BigDecimal.ZERO)).isEmpty();
	}

	@Example
	void shrinkByRemovingDecimalsFirst() {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-10.0), new BigDecimal(10.0), 2);
		Set<BigDecimal> candidates = shrinker.nextCandidates(new BigDecimal("2.15"));

		assertThat(candidates).containsOnly(new BigDecimal("2.1"), new BigDecimal("2.2"));
	}

	@Example
	void shrinkByRemovingLastDecimal() {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-10.0), new BigDecimal(10.0), 2);
		Set<BigDecimal> candidates = shrinker.nextCandidates(new BigDecimal("2.1"));

		assertThat(candidates).containsOnly(new BigDecimal("2"), new BigDecimal("3"));
	}

	@Example
	void shrinkNegativeByRemovingDecimalsFirst() {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-10.0), new BigDecimal(10.0), 2);
		Set<BigDecimal> candidates = shrinker.nextCandidates(new BigDecimal("-3.99"));

		assertThat(candidates).containsOnly(new BigDecimal("-3.9"), new BigDecimal("-4.0"));
	}

	@Example
	void withNoDecimalsShrinkLikeIntegrals() {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-10.0), new BigDecimal(10.0), 2);
		Set<BigDecimal> candidates = shrinker.nextCandidates(new BigDecimal(5.0));

		assertThat(candidates).containsExactlyInAnyOrder(new BigDecimal(3.0), new BigDecimal(4.0));
	}

	@Example
	void if0isInRangeDistanceIsDistanceTo0ShiftedByPrecision() {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-10.0), new BigDecimal(10.0), 2);
		assertThat(shrinker.distance(new BigDecimal("10.0"))).isEqualTo(1000);
		assertThat(shrinker.distance(new BigDecimal("9.55"))).isEqualTo(955);
		assertThat(shrinker.distance(new BigDecimal("9.559"))).isEqualTo(955);
		assertThat(shrinker.distance(new BigDecimal("0.1"))).isEqualTo(10);
		assertThat(shrinker.distance(new BigDecimal("0.0"))).isEqualTo(0);
		assertThat(shrinker.distance(new BigDecimal("-10.0"))).isEqualTo(1000);
		assertThat(shrinker.distance(new BigDecimal("-9.55"))).isEqualTo(955);
		assertThat(shrinker.distance(new BigDecimal("-9.559"))).isEqualTo(955);
		assertThat(shrinker.distance(new BigDecimal("-0.1"))).isEqualTo(10);
	}

	@Example
	void if0isOutsideRangeDistanceIsDistanceToShrinkTarget() {
		ShrinkCandidates<BigDecimal> shrinkerAboveZero = new BigDecimalShrinkCandidates(new BigDecimal(10.0), new BigDecimal(100.0), 2);
		assertThat(shrinkerAboveZero.distance(new BigDecimal(20.1))).isEqualTo(1010);
		assertThat(shrinkerAboveZero.distance(new BigDecimal(10.0))).isEqualTo(0);

		ShrinkCandidates<BigDecimal> shrinkerBelowZero = new BigDecimalShrinkCandidates(new BigDecimal(-100.0), new BigDecimal(-10.0), 2);
		assertThat(shrinkerBelowZero.distance(new BigDecimal(-20.1))).isEqualTo(1010);
		assertThat(shrinkerBelowZero.distance(new BigDecimal(-10.0))).isEqualTo(0);
	}

	@Property(tries = 10000)
	void aValueIsNeverShrunkToItself(@ForAll @DoubleRange(min = -100000, max = 100000) @Scale(4) BigDecimal aValue) {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-Double.MAX_VALUE + 1),
				new BigDecimal(Double.MAX_VALUE - 1), 4);
		Set<BigDecimal> candidates = shrinker.nextCandidates(aValue);
		assertThat(candidates).doesNotContain(aValue);
	}

	@Property(tries = 10000)
	void shrinkingWillAlwaysConvergeToZero(@ForAll @DoubleRange(min = -100, max = 100) @Scale(15) BigDecimal aValue) {
		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(new BigDecimal(-100.0), new BigDecimal(100.0), 15);
		ShrinkableValue<BigDecimal> shrinkableValue = new ShrinkableValue<>(aValue, shrinker);
		ValueShrinker<BigDecimal> valueShrinker = new ValueShrinker<>(shrinkableValue);
		BigDecimal shrunkValue = valueShrinker.shrink(MockFalsifier.falsifyAll(), null).shrunkValue().value();
		assertThat(shrunkValue).isCloseTo(BigDecimal.ZERO, Offset.offset(BigDecimal.ZERO)); // can be + or - 0.0
	}

}
