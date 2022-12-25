package net.jqwik.engine.properties.shrinking;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("EqualsWithItself")
@Group
@Label("ShrinkingDistance")
class ShrinkingDistanceTests {

	@Example
	void append() {
		assertThat(ShrinkingDistance.of(1, 2).append(ShrinkingDistance.of(3, 4)))
			.isEqualTo(ShrinkingDistance.of(1, 2, 3, 4));
	}

	@Example
	void size() {
		assertThat(ShrinkingDistance.of(1).size()).isEqualTo(1);
		assertThat(ShrinkingDistance.of(1, 2, 3, 4).size()).isEqualTo(4);
	}

	@Example
	@Label("ShrinkingDistance.MAX")
	void maximumDistance() {
		assertThat(ShrinkingDistance.MAX.compareTo(ShrinkingDistance.MAX)).isEqualTo(0);

		assertThat(ShrinkingDistance.MAX.compareTo(ShrinkingDistance.of(1))).isEqualTo(1);
		assertThat(ShrinkingDistance.of(1).compareTo(ShrinkingDistance.MAX)).isEqualTo(-1);

		assertThat(ShrinkingDistance.MAX.compareTo(ShrinkingDistance.of(1, 1))).isEqualTo(1);
		assertThat(ShrinkingDistance.of(1, 1).compareTo(ShrinkingDistance.MAX)).isEqualTo(-1);

		assertThat(ShrinkingDistance.MAX.compareTo(ShrinkingDistance.of(Long.MAX_VALUE, 1))).isEqualTo(1);
		assertThat(ShrinkingDistance.of(Long.MAX_VALUE, 1).compareTo(ShrinkingDistance.MAX)).isEqualTo(-1);
	}

	@Group
	class Comparing {
		@Example
		void compareSingleDimension() {
			assertThat(ShrinkingDistance.of(1).compareTo(ShrinkingDistance.of(1))).isEqualTo(0);
			assertThat(ShrinkingDistance.of(1).compareTo(ShrinkingDistance.of(2))).isLessThan(0);
		}

		@Example
		void comparingToMINandMAX() {
			assertThat(ShrinkingDistance.of(1).compareTo(ShrinkingDistance.MIN)).isEqualTo(1);
			assertThat(ShrinkingDistance.of(0, 0).compareTo(ShrinkingDistance.MIN)).isEqualTo(0);

			assertThat(ShrinkingDistance.of(1).compareTo(ShrinkingDistance.MAX)).isEqualTo(-1);

			assertThat(ShrinkingDistance.MIN.compareTo(ShrinkingDistance.MAX)).isEqualTo(-1);
		}

		@Example
		void compareMultiDimension() {
			assertThat(ShrinkingDistance.of(2, 1).compareTo(ShrinkingDistance.of(2, 1))).isEqualTo(0);
			assertThat(ShrinkingDistance.of(2, 1).compareTo(ShrinkingDistance.of(2, 2))).isLessThan(0);

			assertThat(ShrinkingDistance.of(2, 1, 5, 4).compareTo(ShrinkingDistance.of(2, 1, 5, 4))).isEqualTo(0);
			assertThat(ShrinkingDistance.of(2, 1, 5, 0).compareTo(ShrinkingDistance.of(2, 1, 5, 1))).isLessThan(0);

			assertThat(ShrinkingDistance.of(2, 1).compareTo(ShrinkingDistance.of(1, 2))).isGreaterThan(0);
			assertThat(ShrinkingDistance.of(1, 2).compareTo(ShrinkingDistance.of(2, 1))).isLessThan(0);
		}

		@Example
		void compareDifferentDimensions() {
			assertThat(ShrinkingDistance.of(2, 0).compareTo(ShrinkingDistance.of(2))).isEqualTo(0);
			assertThat(ShrinkingDistance.of(2).compareTo(ShrinkingDistance.of(2, 0))).isEqualTo(0);

			assertThat(ShrinkingDistance.of(2).compareTo(ShrinkingDistance.of(2, 1))).isLessThan(0);
			assertThat(ShrinkingDistance.of(2, 1).compareTo(ShrinkingDistance.of(2))).isGreaterThan(0);
		}

	}

	@Group
	@Label("forCollection()")
	class ForCollections {

		@Example
		void emptyCollection() {
			ShrinkingDistance distance = ShrinkingDistance.forCollection(Collections.emptyList());
			assertThat(distance).isEqualTo(ShrinkingDistance.MIN);
		}

		@Example
		void uniformCollection() {
			Collection<Shrinkable<Integer>> elements = asList(
				new OneStepShrinkable(1),
				new OneStepShrinkable(2),
				new OneStepShrinkable(3),
				new OneStepShrinkable(0)
			);
			ShrinkingDistance distance = ShrinkingDistance.forCollection(elements);

			int sizeOfCollection = 4;
			int sumOfElementDistances = 6;
			assertThat(distance).isEqualTo(ShrinkingDistance.of(sizeOfCollection, sumOfElementDistances));
		}

		@Example
		void nonUniformCollection() {
			Collection<Shrinkable<String>> elements = asList(
				Shrinkable.unshrinkable("a"), // [0]
				Shrinkable.unshrinkable("b", ShrinkingDistance.of(1)), // [1]
				ShrinkableStringTests.createShrinkableString("bcd", 0) // [3, 6]
			);
			ShrinkingDistance distance = ShrinkingDistance.forCollection(elements);
			assertThat(distance).isEqualTo(ShrinkingDistance.of(3, 4, 6));
		}
	}

	@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
	@Group
	@Label("combine()")
	class Combine {
		@Example
		void combiningNoShrinkablesIsNotAllowed() {
			assertThatThrownBy(() -> ShrinkingDistance.combine(Collections.emptyList()))
				.isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void zeroDistance() {
			ShrinkingDistance distance = ShrinkingDistance.combine(
				asList(Shrinkable.unshrinkable("hello"))
			);
			assertThat(distance).isEqualTo(ShrinkingDistance.of(0));
		}

		@Example
		void simpleDistance() {
			ShrinkingDistance distance = ShrinkingDistance.combine(
				asList(Shrinkable.unshrinkable(42, ShrinkingDistance.of(42)))
			);
			assertThat(distance).isEqualTo(ShrinkingDistance.of(42));
		}

		@Example
		void several() {
			ShrinkingDistance distance = ShrinkingDistance.combine(asList(
				Shrinkable.unshrinkable(42, ShrinkingDistance.of(42)),
				Shrinkable.unshrinkable("ab", ShrinkingDistance.of(2, 3)),
				Shrinkable.unshrinkable(asList(1, 2, 3), ShrinkingDistance.of(4, 5, 6))
			));
			assertThat(distance).isEqualByComparingTo(ShrinkingDistance.of(42, 2, 3, 4, 5, 6));
		}

	}

	@Group
	@Label("plus()")
	class Plus {

		@Example
		void noOverflow() {
			assertThat(ShrinkingDistance.of(2).plus(ShrinkingDistance.of(3)))
				.isEqualTo(ShrinkingDistance.of(5));
			assertThat(ShrinkingDistance.of(2, 3, 4).plus(ShrinkingDistance.of(3, 4, 5)))
				.isEqualTo(ShrinkingDistance.of(5, 7, 9));
			assertThat(ShrinkingDistance.of(2).plus(ShrinkingDistance.of(3, 4, 5)))
				.isEqualTo(ShrinkingDistance.of(5, 4, 5));
		}

		@Example
		void overflow() {
			assertThat(ShrinkingDistance.of(Long.MAX_VALUE).plus(ShrinkingDistance.of(1)))
				.isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE));
		}
	}

}
