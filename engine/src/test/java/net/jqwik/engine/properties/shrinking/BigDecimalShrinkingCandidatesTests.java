package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

import static org.assertj.core.api.Assertions.*;

@Group
class BigDecimalShrinkingCandidatesTests {

	@Example
	void shrinkFrom0DoesNotShrink() {
		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(-10.0, 10.0).map(BigDecimal::new), BigDecimal.ZERO);
		assertThat(shrinker.candidatesFor(BigDecimal.ZERO)).isEmpty();
	}

	@Example
	void shrinkByRemovingDecimalsAndShrinkingIntegralPart() {
		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(-10.0, 10.0).map(BigDecimal::new), BigDecimal.ZERO);
		Set<BigDecimal> candidates = shrinker.candidatesFor(new BigDecimal("2.15"));

		assertThat(candidates).containsOnly( //
			new BigDecimal("0"), //
			new BigDecimal("1"), //
			new BigDecimal("2.1"), //
			new BigDecimal("2.2") //
		);
	}

	@Example
	void shrinkWillNotShrinkToDecimalsOutsideRange() {
		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(new BigDecimal("2.11"), new BigDecimal("10")), new BigDecimal("2.11"));
		Set<BigDecimal> candidates = shrinker.candidatesFor(new BigDecimal("2.15"));

		assertThat(candidates).containsOnly(new BigDecimal("2.2"));
	}

	@Example
	void shrinkByRemovingLastDecimal() {
		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(-10.0, 10.0).map(BigDecimal::new), BigDecimal.ZERO);
		Set<BigDecimal> candidates = shrinker.candidatesFor(new BigDecimal("2.1"));

		assertThat(candidates).contains( //
			new BigDecimal("2"), //
			new BigDecimal("3") //
		);
	}

	@Example
	void shrinkNegativeByRemovingDecimalsAndShrinkingIntegral() {
		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(-10.0, 10.0).map(BigDecimal::new), BigDecimal.ZERO);
		Set<BigDecimal> candidates = shrinker.candidatesFor(new BigDecimal("-3.99"));

		assertThat(candidates).containsOnly( //
			new BigDecimal("0"), //
			new BigDecimal("-1"), //
			new BigDecimal("-2"), //
			new BigDecimal("-3.9"), //
			new BigDecimal("-4.0") //
		);
	}

	@Example
	void withNoDecimalsShrinkLikeIntegrals() {
		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(-10.0, 10.0).map(BigDecimal::new), BigDecimal.ZERO);
		Set<BigDecimal> candidates = shrinker.candidatesFor(new BigDecimal(5.0));

		assertThat(candidates).containsOnly( //
			new BigDecimal(0.0), //
			new BigDecimal(1.0), //
			new BigDecimal(2.0), //
			new BigDecimal(3.0), //
			new BigDecimal(4.0) //
		);
	}

	@Example
	void integralsWillNotShrinkOutsideRange() {
		ShrinkingCandidates<BigDecimal> shrinker = new BigDecimalShrinkingCandidates(Range.of(0.1, 10.0).map(BigDecimal::new), BigDecimal.ZERO);
		Set<BigDecimal> candidates = shrinker.candidatesFor(new BigDecimal(5.0));

		assertThat(candidates).containsOnly( //
			new BigDecimal(1.0), //
			new BigDecimal(2.0), //
			new BigDecimal(3.0), //
			new BigDecimal(4.0) //
		);
	}

}
