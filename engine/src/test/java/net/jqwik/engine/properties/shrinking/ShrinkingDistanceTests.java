package net.jqwik.engine.properties.shrinking;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Group
@Label("ShrinkingDistance")
class ShrinkingDistanceTests {

	@Group
	class Comparing {
		@Example
		void compareSingleDimension() {
			assertThat(ShrinkingDistance.of(1).compareTo(ShrinkingDistance.of(1))).isEqualTo(0);
			assertThat(ShrinkingDistance.of(1).compareTo(ShrinkingDistance.of(2))).isLessThan(0);
		}

		@Example
		void compareMultiDimension() {
			assertThat(ShrinkingDistance.of(2, 1).compareTo(ShrinkingDistance.of(2, 1))).isEqualTo(0);
			assertThat(ShrinkingDistance.of(2, 1).compareTo(ShrinkingDistance.of(2, 2))).isLessThan(0);

			assertThat(ShrinkingDistance.of(2, 1, 5, 4).compareTo(ShrinkingDistance.of(2, 1, 5, 4))).isEqualTo(0);
			assertThat(ShrinkingDistance.of(2, 1, 5, 0).compareTo(ShrinkingDistance.of(2, 1, 5, 1))).isLessThan(0);

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
			assertThat(distance).isEqualByComparingTo(ShrinkingDistance.of(0, 0));
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
			assertThat(distance).isEqualByComparingTo(ShrinkingDistance.of(sizeOfCollection, sumOfElementDistances));
		}

		@Example
		void nonUniformCollection() {
			Collection<Shrinkable<String>> elements = asList(
				Shrinkable.unshrinkable("hello"), // [0]
				ShrinkableStringTests.createShrinkableString("bcd", 0) // [3, 6]
			);
			ShrinkingDistance distance = ShrinkingDistance.forCollection(elements);
			assertThat(distance).isEqualByComparingTo(ShrinkingDistance.of(2, 3, 6));
		}
	}

	@Group
	@Label("plus()")
	class Plus {

		@Example
		void noOverflow() {
			assertThat(ShrinkingDistance.of(2)
										.plus(ShrinkingDistance.of(3))).isEqualByComparingTo(ShrinkingDistance.of(5));
			assertThat(ShrinkingDistance.of(2, 3, 4)
										.plus(ShrinkingDistance.of(3, 4, 5))).isEqualByComparingTo(ShrinkingDistance.of(5, 7, 9));
			assertThat(ShrinkingDistance.of(2)
										.plus(ShrinkingDistance.of(3, 4, 5))).isEqualByComparingTo(ShrinkingDistance.of(5, 4, 5));
		}

		@Example
		void overflow() {
			assertThat(ShrinkingDistance.of(Long.MAX_VALUE)
										.plus(ShrinkingDistance.of(1))).isEqualByComparingTo(ShrinkingDistance.of(Long.MAX_VALUE));
		}
	}

	@Example
	void append() {
		assertThat(ShrinkingDistance.of(1, 2)
									.append(ShrinkingDistance.of(3, 4))).isEqualByComparingTo(ShrinkingDistance.of(1, 2, 3, 4));

	}
}
