package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class FloatShrinkingTests {

	@Group
	class Doubles {
		@Example
		void valueOutsideRangeDoesNotShrink() {
			ShrinkCandidates<Double> shrinker = new DoubleShrinkCandidates(-10.0, 10.0, 2);
			assertThat(shrinker.nextCandidates(20.0)).isEmpty();
		}

		@Example
		void shrinkFrom0DoesNotShrink() {
			ShrinkCandidates<Double> shrinker = new DoubleShrinkCandidates(-10.0, 10.0, 2);
			assertThat(shrinker.nextCandidates(0.0)).isEmpty();
		}

		@Example
		void shrinkByRemovingDecimalsFirst() {
			ShrinkCandidates<Double> shrinker = new DoubleShrinkCandidates(-10.0, 10.0, 2);
			Set<Double> candidates = shrinker.nextCandidates(2.15);

			assertThat(candidates).containsOnly(2.1, 2.2);
		}

		@Example
		void shrinkNegativeByRemovingDecimalsFirst() {
			ShrinkCandidates<Double> shrinker = new DoubleShrinkCandidates(-10.0, 10.0, 2);
			Set<Double> candidates = shrinker.nextCandidates(- 3.99);

			assertThat(candidates).containsOnly(-3.9, -4.0);
		}

		@Example
		void withNoDecimalsShrinkLikeIntegrals() {
			ShrinkCandidates<Double> shrinker = new DoubleShrinkCandidates(-10.0, 10.0, 2);
			Set<Double> candidates = shrinker.nextCandidates(5.0);

			assertThat(candidates).containsOnly(3.0);
		}

		@Example
		void if0isInRangeDistanceIsDistanceTo0ShiftedByPrecision() {
			ShrinkCandidates<Double> shrinker = new DoubleShrinkCandidates(-10.0, 10.0, 2);
			assertThat(shrinker.distance(10.0)).isEqualTo(1000);
			assertThat(shrinker.distance(9.55)).isEqualTo(955);
			assertThat(shrinker.distance(9.559)).isEqualTo(955);
			assertThat(shrinker.distance(0.1)).isEqualTo(10);
			assertThat(shrinker.distance(0.0)).isEqualTo(0);
			assertThat(shrinker.distance(-10.0)).isEqualTo(1000);
			assertThat(shrinker.distance(-9.55)).isEqualTo(955);
			assertThat(shrinker.distance(-9.559)).isEqualTo(955);
			assertThat(shrinker.distance(-0.1)).isEqualTo(10);
		}

		@Example
		void if0isOutsideRangeDistanceIsDistanceToShrinkTarget() {
			ShrinkCandidates<Double> shrinkerAboveZero = new DoubleShrinkCandidates(10.0, 100.0, 2);
			assertThat(shrinkerAboveZero.distance(20.1)).isEqualTo(1010);
			assertThat(shrinkerAboveZero.distance(10.0)).isEqualTo(0);

			ShrinkCandidates<Double> shrinkerBelowZero = new DoubleShrinkCandidates(-100.0, -10.0, 2);
			assertThat(shrinkerBelowZero.distance(-20.1)).isEqualTo(1010);
			assertThat(shrinkerBelowZero.distance(-10.0)).isEqualTo(0);
		}


	}
}
