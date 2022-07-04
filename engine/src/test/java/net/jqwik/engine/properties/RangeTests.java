package net.jqwik.engine.properties;

import java.math.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class RangeTests {

	@Example
	void stringRepresentations() {
		assertThat(Range.of(1, 2).toString()).isEqualTo("[1..2]");
		assertThat(Range.of(1, true, 2, true).toString()).isEqualTo("[1..2]");
		assertThat(Range.of(1, false, 2, true).toString()).isEqualTo("]1..2]");
		assertThat(Range.of(1, true, 2, false).toString()).isEqualTo("[1..2[");
		assertThat(Range.of(1, false, 2, false).toString()).isEqualTo("]1..2[");
	}

	@Property(tries = 100)
	void bordersAreWithinRangeIfIncluded(@ForAll BigDecimal min, @ForAll BigDecimal max) {
		Assume.that(min.compareTo(max) <= 0);
		Range<BigDecimal> range = Range.of(min, max);
		assertThat(range.includes(min)).isTrue();
		assertThat(range.includes(max)).isTrue();

		assertThat(range.includes(min.subtract(BigDecimal.ONE))).isFalse();
		assertThat(range.includes(max.add(BigDecimal.ONE))).isFalse();
	}

	@Property(tries = 100)
	void minIsOutsideRangeIfExcluded(@ForAll BigDecimal min) {
		BigDecimal max = min.add(BigDecimal.TEN);
		Range<BigDecimal> range = Range.of(min, false, max, true);
		assertThat(range.includes(min)).isFalse();
		assertThat(range.includes(max)).isTrue();

		BigDecimal inbetween = min.add(BigDecimal.ONE);
		assertThat(range.includes(inbetween)).isTrue();
	}

	@Property(tries = 100)
	void maxIsOutsideRangeIfExcluded(@ForAll BigDecimal min) {
		BigDecimal max = min.add(BigDecimal.TEN);
		Range<BigDecimal> range = Range.of(min, true, max, false);
		assertThat(range.includes(min)).isTrue();
		assertThat(range.includes(max)).isFalse();

		BigDecimal inbetween = min.add(BigDecimal.ONE);
		assertThat(range.includes(inbetween)).isTrue();
	}

	@Property(tries = 100)
	void singularRange(@ForAll BigDecimal min) {
		Range<BigDecimal> range = Range.of(min, min);
		assertThat(range.isSingular()).isTrue();
	}

	@Example
	void cannotCreateSingularWithBorderExcluded(@ForAll BigDecimal any) {
		assertThatThrownBy(() -> Range.of(any, false, any, false)).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void cannotCreateMinGreaterThanMax(@ForAll BigDecimal any) {
		assertThatThrownBy(() -> Range.of(any, any.subtract(BigDecimal.ONE))).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void equality() {
		Range<Integer> range1 = Range.of(1, 2);
		Range<Integer> range2 = Range.of(1, 2);
		Range<Integer> range3 = Range.of(1, 3);
		assertThat(range1).isEqualTo(range2);
		assertThat(range1).isNotEqualTo(range3);
	}
}
