package net.jqwik.engine.properties.shrinking;

import java.math.*;

import org.assertj.core.data.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.properties.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;
import static net.jqwik.engine.properties.arbitraries.randomized.RandomDecimalGenerators.*;

class NEW_ShrinkableBigDecimalTests {

	@Example
	void creation() {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal("25.23", Range.of(-100.0, 100.0));
		assertThat(shrinkable.value()).isEqualTo(new BigDecimal("25.23"));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2523));
	}

	@Example
	void cannotCreateValueOutsideRange() {
		assertThatThrownBy(
			() -> createShrinkableBigDecimal("25", Range.of(50.0, 100.0)))
			.isInstanceOf(JqwikException.class);
	}

	@Example
	void shrinkingDistances() {
		assertThat(createShrinkableBigDecimal("25.23", Range.of(-100.0, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(2523));

		assertThat(createShrinkableBigDecimal("25.23", Range.of(20.0, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(523));

		assertThat(createShrinkableBigDecimal("25.23", Range.of(24.5, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(73));

		assertThat(createShrinkableBigDecimal("-25.23", Range.of(-100.0, -24.5)).distance())
			.isEqualTo(ShrinkingDistance.of(73));

		assertThat(createShrinkableBigDecimal("-52.32", Range.of(-100.0, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(5232));

		assertThat(createShrinkableBigDecimal("-52.32", Range.of(-100.0, -50.0)).distance())
			.isEqualTo(ShrinkingDistance.of(232));

		assertThat(createShrinkableBigDecimal("25.000", Range.of(-100.0, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(25000));

		assertThat(createShrinkableBigDecimal("2222222222.1111111111", Range.of(-10000000000.0, 10000000000.0)).distance())
			.isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE));

	}

	@Example
	void shrinkingDistanceWithExplicitShrinkingTarget() {
		assertThat(createShrinkableBigDecimal("25.23", Range.of(-100.0, 100.0), new BigDecimal("23.5")).distance())
			.isEqualTo(ShrinkingDistance.of(173));

		assertThat(createShrinkableBigDecimal("-25.23", Range.of(-100.0, 100.0), new BigDecimal("-10.1")).distance())
			.isEqualTo(ShrinkingDistance.of(1513));
	}

	@Example
	void shrinkWithFilter() {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal("31.55", Range.of(-100.0, 100.0));

		TestingFalsifier<BigDecimal> falsifier = aBigDecimal -> aBigDecimal.doubleValue() < 24.9;
		Falsifier<BigDecimal> filteredFalsifier =
			falsifier.withFilter(aBigDecimal -> aBigDecimal.remainder(BigDecimal.valueOf(2)).longValue() == 1);

		BigDecimal shrunkValue = shrinkToMinimal(shrinkable, filteredFalsifier, null);
		assertThat(shrunkValue.longValueExact()).isEqualTo(25);
	}

	@Property(tries = 100)
	void shrinkingToClosestDecimalWhenMinimumIsNotIncluded(@ForAll @BigRange(min = "1.01", max = "1000000000") @Scale(2) BigDecimal value) {
		Range<Double> doubleRange = Range.of(1.0, false, 1000000000.0, true);
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal(value.toPlainString(), doubleRange, 2);
		BigDecimal shrunkValue = shrinkToMinimal(shrinkable, (TestingFalsifier<BigDecimal>) ignore -> false, null);
		assertThat(shrunkValue).isEqualByComparingTo("1.01");
	}

	@Property(tries = 100)
	void shrinkingToClosestDecimalWhenMaximumIsNotIncluded(@ForAll @BigRange(min = "-1000000", max = "-1.01") @Scale(2) BigDecimal value) {
		Range<Double> doubleRange = Range.of(-1000000.0, true, -1.0, false);
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal(value.toPlainString(), doubleRange, 2);
		BigDecimal shrunkValue = shrinkToMinimal(shrinkable, (TestingFalsifier<BigDecimal>) ignore -> false, null);
		assertThat(shrunkValue).isEqualByComparingTo("-1.01");
	}

	@Property(tries = 100)
	void shrinkingWillAlwaysConvergeToZero(@ForAll @BigRange(min = "-1000000000", max = "1000000000") @Scale(15) BigDecimal aValue) {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal(aValue.toPlainString(), Range.of(-1000000000.0, 1000000000.0));
		BigDecimal shrunkValue = shrinkToMinimal(shrinkable, (TestingFalsifier<BigDecimal>) ignore -> false, null);
		assertThat(shrunkValue).isEqualByComparingTo(BigDecimal.ZERO);
	}

	@Property(tries = 100)
	void shrinkToExplicitShrinkingTarget(
		@ForAll @BigRange(min = "-1000", max = "1000") BigDecimal aValue,
		@ForAll @BigRange(min = "-100", max = "100") BigDecimal shrinkingTarget
	) {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal(aValue.toPlainString(), Range.of(-1000.0, 1000.0), shrinkingTarget);
		BigDecimal shrunkValue = shrinkToMinimal(shrinkable, (TestingFalsifier<BigDecimal>) ignore -> false, null);

		// Allow offset to max 1.0 because decimals are shrunk away if possible
		Offset<BigDecimal> allowedOffset = Offset.offset(new BigDecimal(1));
		assertThat(shrunkValue).isCloseTo(shrinkingTarget, allowedOffset);
	}

	private Shrinkable<BigDecimal> createShrinkableBigDecimal(String numberString, Range<Double> doubleRange) {
		BigDecimal bigDecimalValue = new BigDecimal(numberString);
		Range<BigDecimal> bigDecimalRange = doubleRange.map(BigDecimal::new);
		int scale = bigDecimalValue.scale();
		BigDecimal bigDecimalShrinkingTarget = defaultShrinkingTarget(bigDecimalRange, scale);
		return createShrinkable(bigDecimalValue, bigDecimalRange, scale, bigDecimalShrinkingTarget);
	}

	private Shrinkable<BigDecimal> createShrinkableBigDecimal(String numberString, Range<Double> doubleRange, int scale) {
		BigDecimal bigDecimalValue = new BigDecimal(numberString);
		Range<BigDecimal> bigDecimalRange = doubleRange.map(BigDecimal::new);
		BigDecimal bigDecimalShrinkingTarget = defaultShrinkingTarget(bigDecimalRange, scale);
		return createShrinkable(bigDecimalValue, bigDecimalRange, scale, bigDecimalShrinkingTarget);
	}

	private Shrinkable<BigDecimal> createShrinkableBigDecimal(String numberString, Range<Double> doubleRange, BigDecimal shrinkingTarget) {
		Range<BigDecimal> bigDecimalRange = doubleRange.map(BigDecimal::new);
		BigDecimal value = new BigDecimal(numberString);
		return createShrinkable(value, bigDecimalRange, value.scale(), shrinkingTarget);
	}

	private Shrinkable<BigDecimal> createShrinkable(
		final BigDecimal bigDecimalValue,
		final Range<BigDecimal> bigDecimalRange,
		final int scale,
		final BigDecimal bigDecimalShrinkingTarget
	) {
		BigInteger shrinkingTarget = unscaledBigInteger(bigDecimalShrinkingTarget, scale);
		Range<BigInteger> bigIntegerRange = unscaledBigIntegerRange(bigDecimalRange, scale);
		BigInteger bigIntegerValue = unscaledBigInteger(bigDecimalValue, scale);
		ShrinkableBigInteger shrinkableBigInteger = new ShrinkableBigInteger(
			bigIntegerValue,
			bigIntegerRange,
			shrinkingTarget
		);
		return shrinkableBigInteger.map(bigInteger -> scaledBigDecimal(bigInteger, scale));
	}

}
