package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("ShrinkableBigInteger")
class ShrinkableBigIntegerTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<BigInteger> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<BigInteger>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(25, Range.of(-100L, 100L));
		assertThat(shrinkable.value()).isEqualTo(BigInteger.valueOf(25));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(25));
	}

	@Example
	void cannotCreateValueOutsideRange() {
		assertThatThrownBy( //
			() -> createShrinkableBigInteger(25, Range.of(50L, 100L))) //
			.isInstanceOf(JqwikException.class);
	}

	@Example
	void shrinkingDistanceIsDistanceToShrinkingTarget() {
		assertThat(
			createShrinkableBigInteger(25, Range.of(-100L, 100L)).distance())
			.isEqualTo(ShrinkingDistance.of(25));
		assertThat(
			createShrinkableBigInteger(-25, Range.of(-100L, 100L)).distance())
			.isEqualTo(ShrinkingDistance.of(25));
		assertThat(
			createShrinkableBigInteger(25, Range.of(5L, 100L)).distance())
			.isEqualTo(ShrinkingDistance.of(20));
		assertThat(
			createShrinkableBigInteger(-25, Range.of(-100L, -5L)).distance())
			.isEqualTo(ShrinkingDistance.of(20));
	}

	@Example
	void shrinkingDistanceWithExplicitShrinkingTarget() {
		assertThat(
			createShrinkableBigInteger(25, Range.of(-100L, 100L), 100L).distance())
			.isEqualTo(ShrinkingDistance.of(75));
		assertThat(
			createShrinkableBigInteger(-25, Range.of(-100L, 100L), -100L).distance())
			.isEqualTo(ShrinkingDistance.of(75));
		assertThat(
			createShrinkableBigInteger(-25, Range.of(-100L, 100L), 100L).distance())
			.isEqualTo(ShrinkingDistance.of(125));
	}

	@Example
	void shrinkingDistanceOutsideLongRange() {
		Range<BigInteger> bigIntegerRange = Range.of( //
			new BigInteger("-1000000000000000000000"), //
			new BigInteger("1000000000000000000000"));

		assertThat(
			new ShrinkableBigInteger(new BigInteger("99999999999999999999"), bigIntegerRange, BigInteger.ZERO).distance())
			.isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE));

		assertThat(
			new ShrinkableBigInteger(new BigInteger("-99999999999999999999"), bigIntegerRange, BigInteger.ZERO).distance())
			.isEqualTo(ShrinkingDistance.of(Long.MAX_VALUE));
	}

	@Example
	@Label("report all falsified")
	void reportFalsified() {
		Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(30, Range.of(-100L, 100L));

		ShrinkingSequence<BigInteger> sequence = shrinkable.shrink(aBigInteger -> aBigInteger.compareTo(BigInteger.valueOf(10)) < 0);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(BigInteger.valueOf(13));
		verify(valueReporter).accept(BigInteger.valueOf(13));

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(BigInteger.valueOf(10));
		verify(valueReporter).accept(BigInteger.valueOf(10));

		assertThat(sequence.next(count, reporter)).isFalse();
		verifyNoMoreInteractions(valueReporter);
	}


	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(100000, Range.of(5L, 500000L));

			ShrinkingSequence<BigInteger> sequence = shrinkable.shrink(aBigInteger -> aBigInteger.compareTo(BigInteger.valueOf(1000)) <= 0);

			while (sequence.next(count, reporter));

			assertThat(sequence.current().value()).isEqualTo(BigInteger.valueOf(1001));
			assertThat(counter.get()).isEqualTo(7);
		}

		@Example
		void withFilter() {
			Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(100000, Range.of(0L, 1000000L));

			Falsifier<BigInteger> falsifier = aBigInteger -> aBigInteger.intValueExact() < 99;
			Falsifier<BigInteger> filteredFalsifier = falsifier.withFilter(aBigInteger -> aBigInteger.intValueExact() % 2 == 0);

			ShrinkingSequence<BigInteger> sequence = shrinkable.shrink(filteredFalsifier);

			while (sequence.next(count, reporter));

			assertThat(sequence.current().value()).isEqualTo(100);
			assertThat(counter.get()).isEqualTo(8);
		}

		@Example
		void upToExplicitShrinkingTarget() {
			Shrinkable<BigInteger> shrinkable = createShrinkableBigInteger(1000, Range.of(5L, 500000L), 5000L);

			ShrinkingSequence<BigInteger> sequence = shrinkable.shrink(aBigInteger -> aBigInteger.compareTo(BigInteger.valueOf(5000)) >= 0);

			while (sequence.next(count, reporter)) ;

			assertThat(sequence.current().value()).isEqualTo(BigInteger.valueOf(4999));
			assertThat(counter.get()).isEqualTo(1);
		}

	}

	private Shrinkable<BigInteger> createShrinkableBigInteger(long number, Range<Long> longRange) {
		Range<BigInteger> bigIntegerRange = longRange.map(BigInteger::valueOf);
		return new ShrinkableBigInteger(
			BigInteger.valueOf(number),
			bigIntegerRange,
			ShrinkableBigInteger.defaultShrinkingTarget(BigInteger.valueOf(number), bigIntegerRange)
		);
	}

	private Shrinkable<BigInteger> createShrinkableBigInteger(long number, Range<Long> longRange, long shrinkingTarget) {
		Range<BigInteger> bigIntegerRange = longRange.map(BigInteger::valueOf);
		return new ShrinkableBigInteger(BigInteger.valueOf(number), bigIntegerRange, BigInteger.valueOf(shrinkingTarget));
	}

}
