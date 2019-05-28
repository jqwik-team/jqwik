package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import org.assertj.core.data.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShrinkableBigDecimalTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<BigDecimal> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<BigDecimal>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal("25.23", Range.of(-100.0, 100.0));
		assertThat(shrinkable.value()).isEqualTo(new BigDecimal("25.23"));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(25, 23));
	}

	@Example
	void cannotCreateValueOutsideRange() {
		assertThatThrownBy( //
			() -> createShrinkableBigDecimal("25", Range.of(50.0, 100.0))) //
			.isInstanceOf(JqwikException.class);
	}

	@Example
	void shrinkingDistances() {
		assertThat(createShrinkableBigDecimal("25.23", Range.of(-100.0, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(25, 23));

		assertThat(createShrinkableBigDecimal("25.23", Range.of(20.0, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(5, 23));

		assertThat(createShrinkableBigDecimal("25.23", Range.of(24.5, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(0, 73));

		assertThat(createShrinkableBigDecimal("-25.23", Range.of(-100.0, -24.5)).distance())
			.isEqualTo(ShrinkingDistance.of(0, 73));

		assertThat(createShrinkableBigDecimal("-52.32", Range.of(-100.0, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(52, 32));

		assertThat(createShrinkableBigDecimal("-52.32", Range.of(-100.0, -50.0)).distance())
			.isEqualTo(ShrinkingDistance.of(2, 32));

		assertThat(createShrinkableBigDecimal("25.000", Range.of(-100.0, 100.0)).distance())
			.isEqualTo(ShrinkingDistance.of(25, 0));

		assertThat(createShrinkableBigDecimal("2222222222.1111111111", Range.of(-10000000000.0, 10000000000.0)).distance())
			.isEqualTo(ShrinkingDistance.of(2222222222L, 1111111111L));

	}

	@Example
	void shrinkingDistanceWithExplicitShrinkingTarget() {
		assertThat(createShrinkableBigDecimal("25.23", Range.of(-100.0, 100.0), new BigDecimal("23.5")).distance())
			.isEqualTo(ShrinkingDistance.of(1, 73));

		assertThat(createShrinkableBigDecimal("-25.23", Range.of(-100.0, 100.0), new BigDecimal("-10.1")).distance())
			.isEqualTo(ShrinkingDistance.of(15, 13));
	}

	@Example
	void shrinkingDistanceOutsideLongRange() {
		Range<BigDecimal> bigDecimalRange = Range.of( //
			new BigDecimal("-1000000000000000000000"), //
			new BigDecimal("1000000000000000000000"));

		assertThat( //
			new ShrinkableBigDecimal(new BigDecimal("99999999999999999999.11"), bigDecimalRange, 0, ShrinkableBigDecimal
				.defaultShrinkingTarget(new BigDecimal("99999999999999999999.11"), bigDecimalRange))
				.distance()) //
							 .isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE, 0));

		assertThat( //
			new ShrinkableBigDecimal(new BigDecimal("-99999999999999999999.11"), bigDecimalRange, 0, ShrinkableBigDecimal
				.defaultShrinkingTarget(new BigDecimal("-99999999999999999999.11"), bigDecimalRange))
				.distance()) //
							 .isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE, 0));

		assertThat( //
			new ShrinkableBigDecimal(new BigDecimal("99.11"), bigDecimalRange, 22, ShrinkableBigDecimal
				.defaultShrinkingTarget(new BigDecimal("99.11"), bigDecimalRange))
				.distance()) //
							 .isEqualTo(ShrinkingDistance.of(99, Long.MAX_VALUE));
	}

	@Example
	@Label("report all falsified")
	void reportFalsified() {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal("30.55", Range.of(-100.0, 100.0));

		ShrinkingSequence<BigDecimal> sequence = shrinkable.shrink(aBigDecimal -> aBigDecimal.compareTo(BigDecimal.valueOf(10)) < 0);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(new BigDecimal("13"));
		verify(valueReporter).accept(new BigDecimal("13"));

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(new BigDecimal("10"));
		verify(valueReporter).accept(new BigDecimal("10"));

		assertThat(sequence.next(count, reporter)).isFalse();
		verifyNoMoreInteractions(valueReporter);
	}

	@Example
	void shrinkWithFilter() {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal("31.55", Range.of(-100.0, 100.0));

		Falsifier<BigDecimal> falsifier = aBigDecimal -> aBigDecimal.doubleValue() < 24.9;
		Falsifier<BigDecimal> filteredFalsifier = falsifier.withFilter(aBigDecimal -> aBigDecimal.remainder(BigDecimal.valueOf(2)).longValue() == 1);

		ShrinkingSequence<BigDecimal> sequence = shrinkable.shrink(filteredFalsifier);


		while (sequence.next(count, reporter)) {
		}

		assertThat(sequence.current().value().longValueExact()).isEqualTo(25);
		assertThat(counter.get()).isEqualTo(6);
	}


	@Property
	void shrinkingWillAlwaysConvergeToZero(@ForAll @BigRange(min = "-1000000000", max = "1000000000") @Scale(15) BigDecimal aValue) {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal(aValue.toPlainString(), Range.of(-1000000000.0, 1000000000.0));
		ShrinkingSequence<BigDecimal> sequence = shrinkable.shrink(ignore -> false);
		while(sequence.next(count, reporter));
		BigDecimal shrunkValue = sequence.current().value();
		// can be + or - 0.0:
		assertThat(shrunkValue).isCloseTo(BigDecimal.ZERO, Offset.offset(BigDecimal.ZERO));
	}

	@Property
	void shrinkToExplicitShrinkingTarget(
		@ForAll @BigRange(min = "-1000", max = "1000") BigDecimal aValue,
		@ForAll @BigRange(min = "-100", max = "100") BigDecimal shrinkingTarget
	) {
		Shrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal(aValue.toPlainString(), Range.of(-1000.0, 1000.0), shrinkingTarget);
		ShrinkingSequence<BigDecimal> sequence = shrinkable.shrink(ignore -> false);
		while(sequence.next(count, reporter));
		BigDecimal shrunkValue = sequence.current().value();
		// Allow offset to max 1.0 because decimals are shrunk away if possible
		Offset<BigDecimal> allowedOffset = Offset.offset(new BigDecimal(1));
		assertThat(shrunkValue).isCloseTo(shrinkingTarget, allowedOffset);
	}

	private Shrinkable<BigDecimal> createShrinkableBigDecimal(String numberString, Range<Double> doubleRange) {
		Range<BigDecimal> bigDecimalRange = doubleRange.map(BigDecimal::new);
		BigDecimal value = new BigDecimal(numberString);
		return new ShrinkableBigDecimal(value, bigDecimalRange, value.scale(), ShrinkableBigDecimal
			.defaultShrinkingTarget(value, bigDecimalRange));
	}

	private Shrinkable<BigDecimal> createShrinkableBigDecimal(String numberString, Range<Double> doubleRange, BigDecimal shrinkingTarget) {
		Range<BigDecimal> bigDecimalRange = doubleRange.map(BigDecimal::new);
		BigDecimal value = new BigDecimal(numberString);
		return new ShrinkableBigDecimal(value, bigDecimalRange, value.scale(), shrinkingTarget);
	}

}
