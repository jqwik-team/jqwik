package net.jqwik.properties.newShrinking;

class ShrinkableBigDecimalTests {

//	@Example
//	void if0isInRangeDistanceIsDistanceTo0ShiftedByPrecision() {
//		ShrinkCandidates<BigDecimal> shrinker = new BigDecimalShrinkCandidates(Range.of(-10.0, 10.0).map(BigDecimal::new), 2);
//		assertThat(shrinker.distance(new BigDecimal("10.0"))).isEqualTo(1000);
//		assertThat(shrinker.distance(new BigDecimal("9.55"))).isEqualTo(955);
//		assertThat(shrinker.distance(new BigDecimal("9.559"))).isEqualTo(955);
//		assertThat(shrinker.distance(new BigDecimal("0.1"))).isEqualTo(10);
//		assertThat(shrinker.distance(new BigDecimal("0.0"))).isEqualTo(0);
//		assertThat(shrinker.distance(new BigDecimal("-10.0"))).isEqualTo(1000);
//		assertThat(shrinker.distance(new BigDecimal("-9.55"))).isEqualTo(955);
//		assertThat(shrinker.distance(new BigDecimal("-9.559"))).isEqualTo(955);
//		assertThat(shrinker.distance(new BigDecimal("-0.1"))).isEqualTo(10);
//	}
//
//	@Example
//	void if0isOutsideRangeDistanceIsDistanceToShrinkTarget() {
//		ShrinkCandidates<BigDecimal> shrinkerAboveZero = new BigDecimalShrinkCandidates(Range.of(10.0, 100.0).map(BigDecimal::new), 2);
//		assertThat(shrinkerAboveZero.distance(new BigDecimal(20.1))).isEqualTo(1010);
//		assertThat(shrinkerAboveZero.distance(new BigDecimal(10.0))).isEqualTo(0);
//
//		ShrinkCandidates<BigDecimal> shrinkerBelowZero = new BigDecimalShrinkCandidates(Range.of(-100.0, -10.0).map(BigDecimal::new), 2);
//		assertThat(shrinkerBelowZero.distance(new BigDecimal(-20.1))).isEqualTo(1010);
//		assertThat(shrinkerBelowZero.distance(new BigDecimal(-10.0))).isEqualTo(0);
//	}


//	@Property(tries = 10000)
//	void aValueIsNeverShrunkToItself(@ForAll @BigRange(min = "-100000", max = "100000") @Scale(4) BigDecimal aValue) {
//		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(new BigDecimal(-Double.MAX_VALUE + 1), new BigDecimal(Double.MAX_VALUE - 1)), BigDecimal.ZERO);
//		Set<BigDecimal> candidates = shrinker.candidatesFor(aValue);
//		assertThat(candidates).doesNotContain(aValue);
//	}
//
//	@Property(tries = 10000)
//	void shrinkingWillAlwaysConvergeToZero(@ForAll @BigRange(min = "-100", max = "100") @Scale(15) BigDecimal aValue) {
//		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(-100.0, 100.0).map(BigDecimal::new), BigDecimal.ZERO);
//		ShrinkableValue<BigDecimal> shrinkableValue = new ShrinkableValue<>(aValue, shrinker);
//		ValueShrinker<BigDecimal> valueShrinker = new ValueShrinker<>(shrinkableValue, ignore -> {}, ShrinkingMode.FULL, ignore -> {});
//		BigDecimal shrunkValue = valueShrinker.shrink(MockFalsifier.falsifyAll(), null).shrunkValue().value();
//		assertThat(shrunkValue).isCloseTo(BigDecimal.ZERO, Offset.offset(BigDecimal.ZERO)); // can be + or - 0.0
//	}


}
