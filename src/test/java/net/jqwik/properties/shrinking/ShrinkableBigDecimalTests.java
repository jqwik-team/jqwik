package net.jqwik.properties.shrinking;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.properties.arbitraries.*;
import org.assertj.core.data.*;

import java.math.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShrinkableBigDecimalTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	@SuppressWarnings("unchecked")
	private Consumer<BigDecimal> reporter = mock(Consumer.class);

	@Example
	void creation() {
		NShrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal("25.23", Range.of(-100.0, 100.0));
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
	void shrinkingDistanceOutsideLongRange() {
		Range<BigDecimal> bigDecimalRange = Range.of( //
			new BigDecimal("-1000000000000000000000"), //
			new BigDecimal("1000000000000000000000"));

		assertThat( //
			new ShrinkableBigDecimal(new BigDecimal("99999999999999999999.11"), bigDecimalRange, 0).distance()) //
			.isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE, 0));

		assertThat( //
			new ShrinkableBigDecimal(new BigDecimal("-99999999999999999999.11"), bigDecimalRange, 0).distance()) //
			.isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE, 0));

		assertThat( //
			new ShrinkableBigDecimal(new BigDecimal("99.11"), bigDecimalRange, 22).distance()) //
			.isEqualTo(ShrinkingDistance.of(99, Long.MAX_VALUE));
	}

	@Example
	@Label("report all falsified")
	void reportFalsified() {
		NShrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal("30.55", Range.of(-100.0, 100.0));

		ShrinkingSequence<BigDecimal> sequence = shrinkable.shrink(aBigDecimal -> aBigDecimal.compareTo(BigDecimal.valueOf(10)) < 0);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(new BigDecimal("13"));
		verify(reporter).accept(new BigDecimal("13"));

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(new BigDecimal("10"));
		verify(reporter).accept(new BigDecimal("10"));

		assertThat(sequence.next(count, reporter)).isFalse();
		verifyNoMoreInteractions(reporter);
	}

	@Example
	void shrinkWithFilter() {
		NShrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal("31.55", Range.of(-100.0, 100.0));

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
		NShrinkable<BigDecimal> shrinkable = createShrinkableBigDecimal(aValue.toPlainString(), Range.of(-1000000000.0, 1000000000.0));
		ShrinkingSequence<BigDecimal> sequence = shrinkable.shrink(ignore -> false);
		while(sequence.next(count, reporter)) {
		}
		BigDecimal shrunkValue = sequence.current().value();
		// can be + or - 0.0:
		assertThat(shrunkValue).isCloseTo(BigDecimal.ZERO, Offset.offset(BigDecimal.ZERO));
	}

	private NShrinkable<BigDecimal> createShrinkableBigDecimal(String numberString, Range<Double> doubleRange) {
		Range<BigDecimal> bigDecimalRange = doubleRange.map(BigDecimal::new);
		BigDecimal value = new BigDecimal(numberString);
		return new ShrinkableBigDecimal(value, bigDecimalRange, value.scale());
	}

}
